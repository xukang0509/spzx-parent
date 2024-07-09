package com.spzx.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.order.domain.OrderItem;
import com.spzx.order.mapper.OrderItemMapper;
import com.spzx.order.service.OrderItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单项信息Service业务层处理
 *
 * @author xukang
 * @date 2024-07-03
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    @Resource
    private OrderItemMapper orderItemMapper;

    /**
     * 查询订单项信息列表
     *
     * @param orderItem 订单项信息
     * @return 订单项信息
     */
    @Override
    public List<OrderItem> selectOrderItemList(OrderItem orderItem) {
        return orderItemMapper.selectOrderItemList(orderItem);
    }

}
