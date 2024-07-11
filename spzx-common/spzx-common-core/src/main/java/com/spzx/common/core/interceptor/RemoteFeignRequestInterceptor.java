package com.spzx.common.core.interceptor;

import com.spzx.common.core.constant.SecurityConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * openfeign的拦截器：可以在远程调用的请求发送前 对请求报文进行处理
 */
@Component
public class RemoteFeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 添加请求头
        requestTemplate.header(SecurityConstants.FROM_SOURCE, SecurityConstants.INNER);
    }
}
