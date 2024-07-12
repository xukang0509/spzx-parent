package com.spzx.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.order.api.domain.OrderInfo;

import java.util.List;

/**
 * 订单Mapper接口
 *
 * @author xukang
 * @date 2024-07-03
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单集合
     */
    List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

}
