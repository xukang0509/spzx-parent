package com.spzx.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.order.domain.OrderInfo;
import com.spzx.order.domain.OrderItem;
import com.spzx.order.mapper.OrderInfoMapper;
import com.spzx.order.mapper.OrderItemMapper;
import com.spzx.order.service.OrderInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单Service业务层处理
 *
 * @author xukang
 * @date 2024-07-03
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderItemMapper orderItemMapper;

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单
     */
    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo) {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    @Override
    public OrderInfo selectOrderInfoById(Long id) {
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        List<OrderItem> orderItems = orderItemMapper.selectList(Wrappers.lambdaQuery(OrderItem.class)
                .eq(OrderItem::getOrderId, id));
        orderInfo.setOrderItemList(orderItems);
        return orderInfo;
    }

}
