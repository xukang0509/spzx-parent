package com.spzx.channel.service.impl;

import com.alibaba.fastjson.JSON;
import com.spzx.channel.service.ItemService;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.*;
import com.spzx.user.api.RemoteUserInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Resource
    private RemoteUserInfoService remoteUserInfoService;
    @Resource
    private RemoteProductService remoteProductService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

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

        // 记录浏览历史
        remoteUserInfoService.saveUserBrowseHistory(skuId, SecurityContextHolder.getUserId());
        
        Map<String, Object> data = new HashMap<>();
        // 获取ProductSku
        CompletableFuture<ProductSku> skuCompletableFuture = CompletableFuture.supplyAsync(() -> {
            R<ProductSku> productSkuRes = remoteProductService.getProductSku(skuId);
            if (productSkuRes.getCode() == R.FAIL) {
                throw new ServiceException(productSkuRes.getMsg());
            }
            data.put("productSku", productSkuRes.getData());
            SkuPrice skuPrice = new SkuPrice();
            BeanUtils.copyProperties(productSkuRes.getData(), skuPrice);
            data.put("skuPrice", skuPrice);
            return productSkuRes.getData();
        }, threadPoolExecutor);

        // 获取Product
        CompletableFuture<Void> productCompletableFuture = skuCompletableFuture.thenAcceptAsync(productSku -> {
            R<Product> productRes = remoteProductService.getProduct(productSku.getProductId());
            if (productRes.getCode() == R.FAIL) {
                throw new ServiceException(productRes.getMsg());
            }
            data.put("product", productRes.getData());
            data.put("sliderUrlList", Arrays.asList(productRes.getData().getSliderUrls().split(",")));
            data.put("specValueList", JSON.parseArray(productRes.getData().getSpecValue()));
        }, threadPoolExecutor);

        // 获取ProductDetails
        CompletableFuture<Void> productDetailsCompletableFuture = skuCompletableFuture.thenAcceptAsync(productSku -> {
            R<ProductDetails> productDetailsRes = remoteProductService.getProductDetails(productSku.getProductId());
            if (productDetailsRes.getCode() == R.FAIL) {
                throw new ServiceException(productDetailsRes.getMsg());
            }
            data.put("detailsImageUrlList", Arrays.asList(productDetailsRes.getData().getImageUrls().split(",")));
        }, threadPoolExecutor);

        // 获取SkuStockVo
        CompletableFuture<Void> skuStockVoCompletableFuture = CompletableFuture.runAsync(() -> {
            R<SkuStockVo> skuStockVoRes = remoteProductService.getSkuStockVo(skuId);
            if (skuStockVoRes.getCode() == R.FAIL) {
                throw new ServiceException(skuStockVoRes.getMsg());
            }
            data.put("skuStockVo", skuStockVoRes.getData());
        }, threadPoolExecutor);

        // 获取商品sku规则详细信息
        CompletableFuture<Void> skuSpecValueMapCompletableFuture = skuCompletableFuture.thenAcceptAsync(productSku -> {
            R<Map<String, Long>> skuSpecValueMapRes = remoteProductService.getSkuSpecValueMap(productSku.getProductId());
            if (skuSpecValueMapRes.getCode() == R.FAIL) {
                throw new ServiceException(skuSpecValueMapRes.getMsg());
            }
            data.put("skuSpecValueMap", skuSpecValueMapRes.getData());
        }, threadPoolExecutor);

        CompletableFuture.allOf(
                skuCompletableFuture,
                productCompletableFuture,
                productDetailsCompletableFuture,
                skuStockVoCompletableFuture,
                skuSpecValueMapCompletableFuture
        ).join();
        return data;
    }
}
