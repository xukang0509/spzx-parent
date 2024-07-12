package com.spzx.user.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.spzx.common.core.annotation.Excel;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户浏览记录对象 user_browse_history
 *
 * @author xukang
 * @date 2024-07-11
 */
@Data
@Schema(description = "用户浏览记录")
public class UserBrowseHistory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Excel(name = "用户ID")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 商品skuID
     */
    @Excel(name = "商品skuID")
    @Schema(description = "商品skuID")
    private Long skuId;

    @Schema(description = "商品sku名称")
    @TableField(exist = false)
    private String skuName;

    @Schema(description = "商品sku缩略图路径")
    @TableField(exist = false)
    private String thumbImg;

    @Schema(description = "商品sku售价")
    @TableField(exist = false)
    private BigDecimal salePrice;
}
