package com.spzx.product.api.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuPrice {
    /**
     * SKU ID
     */
    @Schema(description = "skuId")
    private Long skuId;

    /**
     * 售价
     */
    @Schema(description = "售价")
    private BigDecimal salePrice;

    /**
     * 市场价
     */
    @Schema(description = "市场价")
    private BigDecimal marketPrice;
}
