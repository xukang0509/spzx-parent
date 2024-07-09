package com.spzx.cart.service.impl;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.service.CartService;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuPrice;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RemoteProductService remoteProductService;

    @Override
    public void addToCart(Long skuId, Integer skuNum) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        // 1.构建"用户"购物车hash结构key：user:cart:用户ID
        String cartKey = getCartKey(userId);
        // 2.创建Hash结构绑定操作对象(方便对hash进行操作)
        BoundHashOperations<String, String, CartInfo> hashOps = redisTemplate.boundHashOps(cartKey);
        // 3.判断用户购物车中是否包含该商品 如果包含：数量进行累加(某件商品数量上限99)；不包含：新增购物车商品
        String hashKey = String.valueOf(skuId);
        Integer threadHold = 99;
        if (hashOps.hasKey(hashKey)) {
            // 3.1 说明该商品在购物车中已存在，对数量进行累加，不能超过指定上限99
            CartInfo cartInfo = hashOps.get(hashKey);
            int totalCount = cartInfo.getSkuNum() + skuNum;
            cartInfo.setSkuNum(totalCount > threadHold ? threadHold : totalCount);
            hashOps.put(hashKey, cartInfo);
        } else {
            // 4.判断购物车商品种类(不同sku)总数是否大于50
            Long count = hashOps.size();
            if (count > 50) {
                throw new ServiceException("商品种类数量超过上限！");
            }
            // 3.2 说明购物车中没有该商品，构建购物车对象，存入redis
            CartInfo cartInfo = new CartInfo();
            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum > threadHold ? threadHold : skuNum);
            cartInfo.setCreateTime(new Date());
            cartInfo.setCreateBy(SecurityContextHolder.getUserName());
            cartInfo.setUpdateTime(new Date());
            cartInfo.setUpdateBy(SecurityContextHolder.getUserName());
            // 3.2.1 远程调用商品服务获取商品sku基本信息
            R<ProductSku> productSkuRes = remoteProductService.getProductSku(skuId);
            if (productSkuRes.getCode() == R.FAIL) {
                throw new ServiceException(productSkuRes.getMsg());
            }
            ProductSku productSku = productSkuRes.getData();
            cartInfo.setSkuId(skuId);
            cartInfo.setSkuName(productSku.getSkuName());
            cartInfo.setThumbImg(productSku.getThumbImg());
            cartInfo.setSkuPrice(productSku.getSalePrice());
            cartInfo.setCartPrice(productSku.getSalePrice());

            // 3.3 将购物车商品存入redis
            hashOps.put(hashKey, cartInfo);
        }
    }

    @Override
    public List<CartInfo> getCartList() {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        // 构建"用户"购物车hash结构key：user:cart:用户ID
        String cartKey = getCartKey(userId);
        // 获取数据
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            List<CartInfo> infoList = cartInfoList.stream()
                    .sorted((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime())).toList();
            // 获取skuId列表
            List<Long> skuIdList = infoList.stream().map(CartInfo::getSkuId).toList();
            // 查询商品实时价格
            R<List<SkuPrice>> skuPriceListRes = remoteProductService.getSkuPriceList(skuIdList);
            if (skuPriceListRes.getCode() == R.FAIL) {
                throw new ServiceException(skuPriceListRes.getMsg());
            }
            Map<Long, BigDecimal> skuIdToPriceMap = skuPriceListRes.getData().stream()
                    .collect(Collectors.toMap(SkuPrice::getSkuId, SkuPrice::getSalePrice));
            infoList.forEach(cartInfo -> {
                // 设置实时价格
                cartInfo.setSkuPrice(skuIdToPriceMap.get(cartInfo.getSkuId()));
            });
            return infoList;
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteCartBySkuId(Long skuId) {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        // 获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOps = redisTemplate.boundHashOps(cartKey);
        hashOps.delete(String.valueOf(skuId));
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked) {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        // 获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOps = redisTemplate.boundHashOps(cartKey);
        if (hashOps.hasKey(String.valueOf(skuId))) {
            CartInfo cartInfo = hashOps.get(String.valueOf(skuId));
            cartInfo.setIsChecked(isChecked);
            hashOps.put(String.valueOf(skuId), cartInfo);
        }
    }

    @Override
    public void allCheckCart(Integer isChecked) {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        // 获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOps = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOps.values();
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            cartInfoList.forEach(cartInfo -> {
                cartInfo.setIsChecked(isChecked);
                hashOps.put(cartInfo.getSkuId().toString(), cartInfo);
            });
        }
    }

    @Override
    public void clearCart() {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        redisTemplate.delete(cartKey);
    }

    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();

        String cartKey = getCartKey(userId);
        List<CartInfo> cartInfoCacheList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartInfoCacheList)) {
            cartInfoList.addAll(cartInfoCacheList.stream()
                    .filter(cartInfo -> cartInfo.getIsChecked() == 1).toList());
        }
        return cartInfoList;
    }

    @Override
    public Boolean updateCartPrice(Long userId) {
        String cartKey = getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOps = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOps.values();
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            for (CartInfo cartInfo : cartInfoList) {
                if (cartInfo.getIsChecked() == 1) {
                    R<SkuPrice> skuPriceRes = remoteProductService.getSkuPrice(cartInfo.getSkuId());
                    if (R.FAIL == skuPriceRes.getCode()) {
                        throw new ServiceException(skuPriceRes.getMsg());
                    }
                    SkuPrice skuPrice = skuPriceRes.getData();
                    cartInfo.setCartPrice(skuPrice.getSalePrice());
                    cartInfo.setSkuPrice(skuPrice.getSalePrice());
                    hashOps.put(cartInfo.getSkuId().toString(), cartInfo);
                }
            }
        }
        return true;
    }

    @Override
    public Boolean deleteCartCheckedList(Long userId) {
        String cartKey = getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOps = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOps.values();
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            for (CartInfo cartInfo : cartInfoList) {
                if (cartInfo.getIsChecked() == 1) {
                    hashOps.delete(cartInfo.getSkuId().toString());
                }
            }
        }
        return true;
    }

    private String getCartKey(Long userId) {
        return "user:cart:" + userId;
    }
}
