package com.spzx.channel.service.impl;

import com.alibaba.fastjson.JSON;
import com.spzx.channel.service.ItemService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    @Resource
    private RemoteProductService remoteProductService;

    @Override
    public Map<String, Object> getProductDetailData(Long skuId) {
        // 获取ProductSku
        R<ProductSku> productSkuRes = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
        if (productSkuRes.getCode() == R.FAIL) {
            throw new ServiceException(productSkuRes.getMsg());
        }
        // 获取Product
        R<Product> productRes = remoteProductService.getProduct(productSkuRes.getData().getProductId(), SecurityConstants.INNER);
        if (productRes.getCode() == R.FAIL) {
            throw new ServiceException(productRes.getMsg());
        }
        // 获取ProductDetails
        R<ProductDetails> productDetailsRes = remoteProductService.getProductDetails(productSkuRes.getData().getProductId(), SecurityConstants.INNER);
        if (productDetailsRes.getCode() == R.FAIL) {
            throw new ServiceException(productDetailsRes.getMsg());
        }
        // 获取SkuStockVo
        R<SkuStockVo> skuStockVoRes = remoteProductService.getSkuStockVo(skuId, SecurityConstants.INNER);
        if (skuStockVoRes.getCode() == R.FAIL) {
            throw new ServiceException(skuStockVoRes.getMsg());
        }
        // 获取商品sku规则详细信息
        R<Map<String, Long>> skuSpecValueMapRes = remoteProductService.getSkuSpecValueMap(productSkuRes.getData().getProductId(), SecurityConstants.INNER);
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
