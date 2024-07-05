package com.spzx.product.api.domain;

import lombok.Data;

@Data
public class SkuStockVo {
    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 可用库存数
     */
    private Integer availableNum;

    /**
     * 销量
     */
    private Integer saleNum;
}
