package com.spzx.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

    @Override
    public void closePayment(String orderNo) {
        // 查询交易记录
        PaymentInfo paymentInfo = paymentInfoService.getOne(Wrappers.lambdaQuery(PaymentInfo.class)
                .eq(PaymentInfo::getOrderNo, orderNo));
        // 判断交易记录是否存在，并且未支付
        if (paymentInfo != null && paymentInfo.getPaymentStatus() == 0) {
            // 产生本地交易记录 要进行关闭
            paymentInfo.setPaymentStatus(-1);
            paymentInfoService.updateById(paymentInfo);

            // 查询支付宝交易记录是否关闭
            if (checkAlipayPayment(orderNo)) {
                closeAlipayTrade(orderNo);
            }
        }

    }
    
    /**
     * 查看是否有交易记录是否可以关闭
     * 官方文档：https://opendocs.alipay.com/open/4e2d51d1_alipay.trade.query?scene=common&pathHash=8abc6ffe
     *
     * @param orderNo 订单号
     * @return false:不可以关闭  true:未关闭，可以关闭
     */
    private Boolean checkAlipayPayment(String orderNo) {
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderNo);
            request.setBizContent(bizContent.toString());
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                // 交易状态：
                // WAIT_BUYER_PAY（交易创建，等待买家付款）
                // TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
                // TRADE_SUCCESS（交易支付成功）
                // TRADE_FINISHED（交易结束，不可退款）
                if ("WAIT_BUYER_PAY".equals(response.getTradeStatus())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭支付宝交易
     * 官方文档：https://opendocs.alipay.com/open/a62e8677_alipay.trade.close?scene=common&pathHash=0801e763
     *
     * @param orderNo 订单号
     */
    private Boolean closeAlipayTrade(String orderNo) {
        try {
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderNo);
            request.setBizContent(bizContent.toString());
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("支付宝关闭交易调用成功");
                return true;
            } else {
                log.info("支付宝关闭交易调用失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
