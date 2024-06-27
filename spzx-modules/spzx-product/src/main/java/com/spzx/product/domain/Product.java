package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品
 */
@Schema(description = "商品")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "spzx-product.product")
public class Product extends BaseEntity {
    /**
     * 商品名称
     */
    @TableField(value = "`name`")
    @Schema(description = "商品名称")
    private String name;

    /**
     * 品牌ID
     */
    @TableField(value = "brand_id")
    @Schema(description = "品牌ID")
    private Long brandId;

    /**
     * 一级分类id
     */
    @TableField(value = "category1_id")
    @Schema(description = "一级分类id")
    private Long category1Id;

    /**
     * 二级分类id
     */
    @TableField(value = "category2_id")
    @Schema(description = "二级分类id")
    private Long category2Id;

    /**
     * 三级分类id
     */
    @TableField(value = "category3_id")
    @Schema(description = "三级分类id")
    private Long category3Id;

    /**
     * 计量单位
     */
    @TableField(value = "unit_name")
    @Schema(description = "计量单位")
    private String unitName;

    /**
     * 轮播图
     */
    @TableField(value = "slider_urls")
    @Schema(description = "轮播图")
    private String sliderUrls;

    /**
     * 商品规格json
     */
    @TableField(value = "spec_value")
    @Schema(description = "商品规格json")
    private String specValue;

    /**
     * 线上状态：0-初始值，1-上架，-1-自主下架
     */
    @TableField(value = "`status`")
    @Schema(description = "线上状态：0-初始值，1-上架，-1-自主下架")
    private Byte status;

    /**
     * 审核状态：0-初始值，1-通过，-1-未通过
     */
    @TableField(value = "audit_status")
    @Schema(description = "审核状态：0-初始值，1-通过，-1-未通过")
    private Byte auditStatus;

    /**
     * 审核信息
     */
    @TableField(value = "audit_message")
    @Schema(description = "审核信息")
    private String auditMessage;
}