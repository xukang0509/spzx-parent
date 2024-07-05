package com.spzx.product.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品对象 product
 */
@Schema(description = "商品")
@Data
@TableName(value = "product")
public class Product extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField(value = "`name`")
    @Schema(description = "商品名称")
    private String name;

    @TableField(value = "brand_id")
    @Schema(description = "品牌ID")
    private Long brandId;

    @TableField(value = "category1_id")
    @Schema(description = "一级分类id")
    private Long category1Id;

    @TableField(value = "category2_id")
    @Schema(description = "二级分类id")
    private Long category2Id;

    @TableField(value = "category3_id")
    @Schema(description = "三级分类id")
    private Long category3Id;

    @TableField(value = "unit_name")
    @Schema(description = "计量单位")
    private String unitName;

    @TableField(value = "slider_urls")
    @Schema(description = "轮播图")
    private String sliderUrls;

    @TableField(value = "spec_value")
    @Schema(description = "商品规格json")
    private String specValue;

    @TableField(value = "`status`")
    @Schema(description = "线上状态：0-初始值，1-上架，-1-自主下架")
    private Integer status;

    @TableField(value = "audit_status")
    @Schema(description = "审核状态：0-初始值，1-通过，-1-未通过")
    private Integer auditStatus;

    @TableField(value = "audit_message")
    @Schema(description = "审核信息")
    private String auditMessage;

    @TableField(exist = false)
    @Schema(description = "品牌名称")
    private String brandName;

    @TableField(exist = false)
    @Schema(description = "一级分类名称")
    private String category1Name;

    @TableField(exist = false)
    @Schema(description = "二级分类名称")
    private String category2Name;

    @TableField(exist = false)
    @Schema(description = "三级分类名称")
    private String category3Name;

    @Schema(description = "商品sku列表")
    @TableField(exist = false)
    private List<ProductSku> productSkuList;

    @Schema(description = "详情图片列表")
    @TableField(exist = false)
    private List<String> detailsImageUrlList;
}