package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品sku
 */
@Schema(description = "商品sku")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "spzx-product.product_sku")
public class ProductSku extends BaseEntity {
    /**
     * 商品编号
     */
    @TableField(value = "sku_code")
    @Schema(description = "商品编号")
    private String skuCode;

    @TableField(value = "sku_name")
    @Schema(description = "")
    private String skuName;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 缩略图路径
     */
    @TableField(value = "thumb_img")
    @Schema(description = "缩略图路径")
    private String thumbImg;

    /**
     * 售价
     */
    @TableField(value = "sale_price")
    @Schema(description = "售价")
    private BigDecimal salePrice;

    /**
     * 市场价
     */
    @TableField(value = "market_price")
    @Schema(description = "市场价")
    private BigDecimal marketPrice;

    /**
     * 成本价
     */
    @TableField(value = "cost_price")
    @Schema(description = "成本价")
    private BigDecimal costPrice;

    /**
     * sku规格信息json
     */
    @TableField(value = "sku_spec")
    @Schema(description = "sku规格信息json")
    private String skuSpec;

    /**
     * 重量
     */
    @TableField(value = "weight")
    @Schema(description = "重量")
    private BigDecimal weight;

    /**
     * 体积
     */
    @TableField(value = "volume")
    @Schema(description = "体积")
    private BigDecimal volume;

    /**
     * 线上状态：0-初始值，1-上架，-1-自主下架
     */
    @TableField(value = "`status`")
    @Schema(description = "线上状态：0-初始值，1-上架，-1-自主下架")
    private Byte status;
}