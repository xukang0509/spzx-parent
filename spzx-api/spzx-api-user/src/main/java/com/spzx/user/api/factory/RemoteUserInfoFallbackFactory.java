package com.spzx.user.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.user.api.RemoteUserInfoService;
import com.spzx.user.api.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 会员服务降级处理
 */
@Component
public class RemoteUserInfoFallbackFactory implements FallbackFactory<RemoteUserInfoService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteUserInfoFallbackFactory.class);

    @Override
    public RemoteUserInfoService create(Throwable cause) {
        log.error("会员服务调用失败:{}", cause.getMessage());
        return new RemoteUserInfoService() {
            @Override
            public R<Boolean> register(UserInfo userInfo) {
                return R.fail("会员注册失败：" + cause.getMessage());
            }
        };
    }
}
