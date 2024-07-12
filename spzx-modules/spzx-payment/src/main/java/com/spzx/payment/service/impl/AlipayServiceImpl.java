package com.spzx.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.spzx.payment.configure.AlipayConfig;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.service.AlipayService;
import com.spzx.payment.service.PaymentInfoService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService {
    @Resource
    private AlipayClient alipayClient;
    @Resource
    private PaymentInfoService paymentInfoService;

    @SneakyThrows
    @Override
    public String submitAlipay(String orderNo) {
        // 保存支付记录
        PaymentInfo paymentInfo = paymentInfoService.savePaymentInfoByOrderNo(orderNo);
        // 生成二维码
        // 创建API对应的request
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        // 同步回调
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        // 异步回调
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url); //在公共参数中设置回跳和通知地址

        // 参数：声明一个map集合
        Map<String, Object> map = new HashMap<>();
        map.put("out_trade_no", paymentInfo.getOrderNo());
        map.put("product_code", "QUICK_WAP_WAY");
        map.put("subject", paymentInfo.getContent());
        //map.put("total_amount", paymentInfo.getAmount());
        map.put("total_amount", "0.01");

        alipayRequest.setBizContent(JSON.toJSONString(map));
        return alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单;
    }
}
