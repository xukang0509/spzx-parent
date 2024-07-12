package com.spzx.payment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.payment.configure.AlipayConfig;
import com.spzx.payment.service.AlipayService;
import com.spzx.payment.service.PaymentInfoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/alipay")
public class AlipayController extends BaseController {
    @Resource
    private AlipayService alipayService;
    @Resource
    private PaymentInfoService paymentInfoService;


    @Operation(summary = "支付宝下单")
    @RequiresLogin
    @RequestMapping("/submitAlipay/{orderNo}")
    @ResponseBody
    public AjaxResult submitAlipay(@PathVariable("orderNo") String orderNo) {
        String form = alipayService.submitAlipay(orderNo);
        return success(form);
    }

    @Operation(summary = "支付宝同步回调")
    @RequestMapping("callback/return")
    public String callback() {
        // 同步回调给用户展示信息
        return "redirect:" + AlipayConfig.return_order_url;
    }

    @Operation(summary = "支付宝异步回调")
    @RequestMapping("callback/notify")
    @ResponseBody
    public String alipayNotify(@RequestParam Map<String, String> paramMap, HttpServletRequest request) {
        log.info("AlipayController...alipayNotify方法执行了...");

        boolean signVerified = false; // 调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCertCheckV1(paramMap,
                    AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        // 交易状态
        String trade_status = paramMap.get("trade_status");

        if (signVerified) {
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，
            //  校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                // 正常的支付成功，我们应该更新交易记录状态
                paymentInfoService.updatePaymentStatus(paramMap, 2);
                return "success";
            }
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回failure
            return "failure";
        }

        return "failure";
    }
}
