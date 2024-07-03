package com.spzx.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.order.domain.OrderItem;

import java.util.List;

/**
 * 订单项信息Mapper接口
 *
 * @author xukang
 * @date 2024-07-03
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 查询订单项信息列表
     *
     * @param orderItem 订单项信息
     * @return 订单项信息集合
     */
    public List<OrderItem> selectOrderItemList(OrderItem orderItem);

}
