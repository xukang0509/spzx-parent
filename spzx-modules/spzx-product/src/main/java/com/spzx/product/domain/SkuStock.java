package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品sku库存表
 */
@Schema(description = "商品sku库存表")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "spzx-product.sku_stock")
public class SkuStock extends BaseEntity {
    /**
     * 商品ID
     */
    @TableField(value = "sku_id")
    @Schema(description = "商品ID")
    private Long skuId;

    /**
     * 总库存数
     */
    @TableField(value = "total_num")
    @Schema(description = "总库存数")
    private Integer totalNum;

    /**
     * 锁定库存
     */
    @TableField(value = "lock_num")
    @Schema(description = "锁定库存")
    private Integer lockNum;

    /**
     * 可用库存
     */
    @TableField(value = "available_num")
    @Schema(description = "可用库存")
    private Integer availableNum;

    /**
     * 销量
     */
    @TableField(value = "sale_num")
    @Schema(description = "销量")
    private Integer saleNum;

    /**
     * 线上状态：0-初始值，1-上架，-1-自主下架
     */
    @TableField(value = "`status`")
    @Schema(description = "线上状态：0-初始值，1-上架，-1-自主下架")
    private Byte status;
}