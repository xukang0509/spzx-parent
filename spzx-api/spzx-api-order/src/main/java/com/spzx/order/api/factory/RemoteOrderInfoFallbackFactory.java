package com.spzx.order.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.order.api.RemoteOrderInfoService;
import com.spzx.order.api.domain.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteOrderInfoFallbackFactory implements FallbackFactory<RemoteOrderInfoService> {
    @Override
    public RemoteOrderInfoService create(Throwable cause) {
        log.error("订单服务调用失败:{}", cause.getMessage());
        return new RemoteOrderInfoService() {
            @Override
            public R<OrderInfo> getByOrderNo(String orderNo) {
                return R.fail("根据订单号获取订单信息失败:" + cause.getMessage());
            }
        };
    }
}
