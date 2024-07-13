package com.spzx.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.domain.OrderForm;
import com.spzx.order.domain.TradeVo;

import java.util.List;

/**
 * 订单Service接口
 *
 * @author xukang
 * @date 2024-07-03
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单集合
     */
    List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    /**
     * 查询订单
     *
     * @param id 订单ID
     * @return 订单
     */
    OrderInfo selectOrderInfoById(Long id);

    TradeVo orderTradeData();

    Long submitOrder(OrderForm orderForm);

    TradeVo buyNow(Long skuId);

    IPage<OrderInfo> userOrderInfoList(Page<OrderInfo> pageParam, Integer orderStatus);

    void processCloseOrder(Long orderId);

    void cancelOrder(Long orderId);

    // 根据订单号获取订单信息
    OrderInfo getByOrderNo(String orderNo);

    void processPaySuccess(String orderNo);
}
