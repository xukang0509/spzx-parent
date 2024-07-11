package com.spzx.product.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 远程商品服务降级处理
 */
@Component
public class RemoteProductFallbackFactory implements FallbackFactory<RemoteProductService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteProductFallbackFactory.class);

    @Override
    public RemoteProductService create(Throwable cause) {
        log.error("商品服务调用失败：{}", cause.getMessage());
        return new RemoteProductService() {
            @Override
            public R<List<ProductSku>> getTopSale() {
                return R.fail("获取畅销商品列表失败：" + cause.getMessage());
            }

            @Override
            public R<TableDataInfo> skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery) {
                return R.fail("获取商品列表失败：" + cause.getMessage());
            }

            @Override
            public R<ProductSku> getProductSku(Long skuId) {
                return R.fail("获取商品SKU失败：" + cause.getMessage());
            }

            @Override
            public R<Product> getProduct(Long productId) {
                return R.fail("获取商品失败：" + cause.getMessage());
            }

            @Override
            public R<ProductDetails> getProductDetails(Long productId) {
                return R.fail("获取商品详细信息失败：" + cause.getMessage());
            }

            @Override
            public R<SkuStockVo> getSkuStockVo(Long skuId) {
                return R.fail("获取sku库存信息失败：" + cause.getMessage());
            }

            @Override
            public R<Map<String, Long>> getSkuSpecValueMap(Long productId) {
                return R.fail("获取商品sku规则详细信息失败：" + cause.getMessage());
            }

            @Override
            public R<List<SkuPrice>> getSkuPriceList(List<Long> skuIds) {
                return R.fail("批量获取商品sku最新价格信息失败：" + cause.getMessage());
            }

            @Override
            public R<SkuPrice> getSkuPrice(Long skuId) {
                return R.fail("批量获取商品sku最新价格信息失败：" + cause.getMessage());
            }

            @Override
            public R<String> checkAndLock(String orderNo, List<SkuLockVo> skuLockVoList) {
                return R.fail("检查与锁定库存失败：" + cause.getMessage());
            }
        };
    }
}
