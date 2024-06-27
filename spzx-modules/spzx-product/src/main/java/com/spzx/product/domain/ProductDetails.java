package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品详情对象 product_details
 */
@Schema(description = "商品详情")
@Data
@TableName(value = "product_details")
public class ProductDetails extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField(value = "product_id")
    @Schema(description = "商品id")
    private Long productId;

    @TableField(value = "image_urls")
    @Schema(description = "详情图片地址")
    private String imageUrls;
}