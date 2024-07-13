package com.spzx.payment.service;

public interface AlipayService {

    String submitAlipay(String orderNo);

    void closePayment(String orderNo);
}
