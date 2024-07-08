package com.spzx.user.api;

import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.api.factory.RemoteUserAddressFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 远程会员地址服务
 */
@FeignClient(contextId = "remoteUserAddressService", value = ServiceNameConstants.USER_SERVICE,
        fallbackFactory = RemoteUserAddressFallbackFactory.class)
public interface RemoteUserAddressService {
    @GetMapping("/userAddress/getUserAddress/{id}")
    R<UserAddress> getUserAddress(@PathVariable("id") Long id);
}
