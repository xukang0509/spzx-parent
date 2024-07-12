package com.spzx.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.payment.domain.PaymentInfo;

import java.util.Map;

/**
 * 付款信息Service接口
 *
 * @author xukang
 * @date 2024-07-12
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    PaymentInfo savePaymentInfoByOrderNo(String orderNo);

    // 更新支付信息
    void updatePaymentStatus(Map<String, String> paramMap, Integer payType);
}
