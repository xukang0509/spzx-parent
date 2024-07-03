package com.spzx.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.order.domain.OrderInfo;

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
}
