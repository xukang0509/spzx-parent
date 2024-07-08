package com.spzx.product.api;

import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.domain.*;
import com.spzx.product.api.factory.RemoteProductFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteProductService", value = ServiceNameConstants.PRODUCT_SERVICE,
        fallbackFactory = RemoteProductFallbackFactory.class)
public interface RemoteProductService {
    @GetMapping("/product/getTopSale")
    R<List<ProductSku>> getTopSale();

    @GetMapping("/product/skuList/{pageNum}/{pageSize}")
    R<TableDataInfo> skuList(@PathVariable("pageNum") Integer pageNum,
                             @PathVariable("pageSize") Integer pageSize,
                             @SpringQueryMap SkuQuery skuQuery);

    @GetMapping("/product/getProductSku/{skuId}")
    R<ProductSku> getProductSku(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/getProduct/{productId}")
    R<Product> getProduct(@PathVariable("productId") Long productId);

    @GetMapping("/product/getProductDetails/{productId}")
    R<ProductDetails> getProductDetails(@PathVariable("productId") Long productId);

    @GetMapping("/product/getSkuStockVo/{skuId}")
    R<SkuStockVo> getSkuStockVo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/getSkuSpecValue/{productId}")
    R<Map<String, Long>> getSkuSpecValueMap(@PathVariable("productId") Long productId);

    @PostMapping("/product/getSkuPriceList")
    R<List<SkuPrice>> getSkuPriceList(@RequestBody List<Long> skuIds);
}
