package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品sku属性表
 */
@Schema(description = "商品sku属性表")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "spzx-product.product_details")
public class ProductDetails extends BaseEntity {
    /**
     * 商品id
     */
    @TableField(value = "product_id")
    @Schema(description = "商品id")
    private Long productId;

    /**
     * 详情图片地址
     */
    @TableField(value = "image_urls")
    @Schema(description = "详情图片地址")
    private String imageUrls;
}