package com.spzx.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.api.RemoteOrderInfoService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.api.domain.OrderItem;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.mapper.PaymentInfoMapper;
import com.spzx.payment.service.PaymentInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 付款信息Service业务层处理
 *
 * @author xukang
 * @date 2024-07-12
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    @Resource
    private RemoteOrderInfoService remoteOrderInfoService;

    @Resource
    private RabbitService rabbitService;


    @Override
    public PaymentInfo savePaymentInfoByOrderNo(String orderNo) {
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(Wrappers.lambdaQuery(PaymentInfo.class)
                .eq(PaymentInfo::getOrderNo, orderNo));
        if (paymentInfo == null) {
            R<OrderInfo> orderInfoRes = remoteOrderInfoService.getByOrderNo(orderNo);
            if (orderInfoRes.getCode() == R.FAIL) {
                throw new ServiceException(orderInfoRes.getMsg());
            }
            OrderInfo orderInfo = orderInfoRes.getData();

            paymentInfo = new PaymentInfo();
            paymentInfo.setUserId(orderInfo.getUserId());
            paymentInfo.setOrderNo(orderNo);
            paymentInfo.setAmount(orderInfo.getTotalAmount());
            paymentInfo.setPaymentStatus(0);

            List<String> strings = new ArrayList<>();
            for (OrderItem orderItem : orderInfo.getOrderItemList()) {
                strings.add(orderItem.getSkuName());
            }
            paymentInfo.setContent(strings.stream().collect(Collectors.joining(" ")));

            paymentInfoMapper.insert(paymentInfo);
        }
        return paymentInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePaymentStatus(Map<String, String> paramMap, Integer payType) {
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(Wrappers.lambdaQuery(PaymentInfo.class)
                .eq(PaymentInfo::getOrderNo, paramMap.get("out_trade_no")));
        if (paymentInfo.getPaymentStatus() == 1) return;

        // 更新支付信息
        paymentInfo.setPayType(payType);
        paymentInfo.setPaymentStatus(1);
        paymentInfo.setTradeNo(paramMap.get("trade_no"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(JSON.toJSONString(paramMap));
        paymentInfoMapper.updateById(paymentInfo);

        // 基于MQ通知订单系统，修改订单状态
        rabbitService.sendMessage(MqConst.EXCHANGE_PAYMENT_PAY, MqConst.ROUTING_PAYMENT_PAY,
                paymentInfo.getOrderNo());
    }
}
