package com.spzx.channel.service.impl;

import com.alibaba.fastjson.JSON;
import com.spzx.channel.service.ItemService;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Resource
    private RemoteProductService remoteProductService;
    @Resource
    private RedissonClient redissonClient;

    // 获取商品详情信息
    @Override
    public Map<String, Object> getProductDetailData(Long skuId) {
        // 远程调用商品微服务接口之前，提前知道用户访问商品skuId是否存在于布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("sku:bloom:filter");
        if (!bloomFilter.contains(skuId)) {
            log.error("用户查询商品sku不存在：{}", skuId);
            //查询数据不存在直接返回空对象
            throw new ServiceException("用户查询商品sku不存在");
        }

        // 获取ProductSku
        R<ProductSku> productSkuRes = remoteProductService.getProductSku(skuId);
        if (productSkuRes.getCode() == R.FAIL) {
            throw new ServiceException(productSkuRes.getMsg());
        }
        // 获取Product
        R<Product> productRes = remoteProductService.getProduct(productSkuRes.getData().getProductId());
        if (productRes.getCode() == R.FAIL) {
            throw new ServiceException(productRes.getMsg());
        }
        // 获取ProductDetails
        R<ProductDetails> productDetailsRes = remoteProductService.getProductDetails(productSkuRes.getData().getProductId());
        if (productDetailsRes.getCode() == R.FAIL) {
            throw new ServiceException(productDetailsRes.getMsg());
        }
        // 获取SkuStockVo
        R<SkuStockVo> skuStockVoRes = remoteProductService.getSkuStockVo(skuId);
        if (skuStockVoRes.getCode() == R.FAIL) {
            throw new ServiceException(skuStockVoRes.getMsg());
        }
        // 获取商品sku规则详细信息
        R<Map<String, Long>> skuSpecValueMapRes = remoteProductService.getSkuSpecValueMap(productSkuRes.getData().getProductId());
        if (skuSpecValueMapRes.getCode() == R.FAIL) {
            throw new ServiceException(skuSpecValueMapRes.getMsg());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("productSku", productSkuRes.getData());
        data.put("product", productRes.getData());
        data.put("skuStockVo", skuStockVoRes.getData());
        data.put("skuSpecValueMap", skuSpecValueMapRes.getData());
        data.put("sliderUrlList", Arrays.asList(productRes.getData().getSliderUrls().split(",")));
        data.put("detailsImageUrlList", Arrays.asList(productDetailsRes.getData().getImageUrls().split(",")));
        data.put("specValueList", JSON.parseArray(productRes.getData().getSpecValue()));
        SkuPrice skuPrice = new SkuPrice();
        BeanUtils.copyProperties(productSkuRes.getData(), skuPrice);
        data.put("skuPrice", skuPrice);
        return data;
    }
}
