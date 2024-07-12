package com.spzx.order.domain;

import com.spzx.order.api.domain.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderForm {

    @Schema(description = "用户流水号")
    private String tradeNo;

    @Schema(description = "送货地址Id")
    private Long userAddressId;

    @Schema(description = "运费")
    private BigDecimal feightFee;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "结算商品列表")
    private List<OrderItem> orderItemList;
}
