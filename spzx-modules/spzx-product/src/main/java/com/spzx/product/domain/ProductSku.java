package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品sku product_sku
 */
@Schema(description = "商品sku")
@Data
@TableName(value = "product_sku")
public class ProductSku extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField(value = "sku_code")
    @Schema(description = "商品编号")
    private String skuCode;

    @TableField(value = "sku_name")
    @Schema(description = "sku名称")
    private String skuName;

    @TableField(value = "product_id")
    @Schema(description = "商品ID")
    private Long productId;

    @TableField(value = "thumb_img")
    @Schema(description = "缩略图路径")
    private String thumbImg;

    @TableField(value = "sale_price")
    @Schema(description = "售价")
    private BigDecimal salePrice;

    @TableField(value = "market_price")
    @Schema(description = "市场价")
    private BigDecimal marketPrice;

    @TableField(value = "cost_price")
    @Schema(description = "成本价")
    private BigDecimal costPrice;

    @TableField(value = "sku_spec")
    @Schema(description = "sku规格信息json")
    private String skuSpec;

    @TableField(value = "weight")
    @Schema(description = "重量")
    private BigDecimal weight;

    @TableField(value = "volume")
    @Schema(description = "体积")
    private BigDecimal volume;

    @TableField(value = "`status`")
    @Schema(description = "线上状态：0-初始值，1-上架，-1-自主下架")
    private Integer status;

    @Schema(description = "sku库存")
    @TableField(exist = false)
    private Integer stockNum;
}