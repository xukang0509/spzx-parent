package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品sku库存表 sku_stock
 */
@Schema(description = "商品sku库存")
@Data
@TableName(value = "sku_stock")
public class SkuStock extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField(value = "sku_id")
    @Schema(description = "商品ID")
    private Long skuId;

    @TableField(value = "total_num")
    @Schema(description = "总库存数")
    private Integer totalNum;

    @TableField(value = "lock_num")
    @Schema(description = "锁定库存数")
    private Integer lockNum;

    @TableField(value = "available_num")
    @Schema(description = "可用库存数")
    private Integer availableNum;

    @TableField(value = "sale_num")
    @Schema(description = "销量")
    private Integer saleNum;

    @TableField(value = "`status`")
    @Schema(description = "线上状态：0-初始值，1-上架，-1-自主下架")
    private Integer status;
}