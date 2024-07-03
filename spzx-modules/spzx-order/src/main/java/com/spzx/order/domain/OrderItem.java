package com.spzx.order.domain;

import com.spzx.common.core.annotation.Excel;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项信息对象 order_item
 *
 * @author xukang
 * @date 2024-07-03
 */
@Data
@Schema(description = "订单项信息")
public class OrderItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * order_id
     */
    @Excel(name = "order_id")
    @Schema(description = "order_id")
    private Long orderId;

    /**
     * 商品sku编号
     */
    @Excel(name = "商品sku编号")
    @Schema(description = "商品sku编号")
    private Long skuId;

    /**
     * 商品sku名字
     */
    @Excel(name = "商品sku名字")
    @Schema(description = "商品sku名字")
    private String skuName;

    /**
     * 商品sku图片
     */
    @Excel(name = "商品sku图片")
    @Schema(description = "商品sku图片")
    private String thumbImg;

    /**
     * 商品sku价格
     */
    @Excel(name = "商品sku价格")
    @Schema(description = "商品sku价格")
    private BigDecimal skuPrice;

    /**
     * 商品购买的数量
     */
    @Excel(name = "商品购买的数量")
    @Schema(description = "商品购买的数量")
    private Integer skuNum;
}
