package com.spzx.cart.api.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "购物车信息")
public class CartInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "skuID")
    private Long skuId;

    @Schema(description = "放入购物车时价格")
    private BigDecimal cartPrice;

    @Schema(description = "实时价格")
    private BigDecimal skuPrice;

    @Schema(description = "数量")
    private Integer skuNum;

    @Schema(description = "图片文件")
    private String thumbImg;

    @Schema(description = "sku名称(冗余)")
    private String skuName;

    @Schema(description = "isChecked")
    private Integer isChecked = 1;
}
