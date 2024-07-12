package com.spzx.user.api;

import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.api.factory.RemoteUserInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 会员服务
 */
@FeignClient(contextId = "remoteUserInfoService", value = ServiceNameConstants.USER_SERVICE,
        fallbackFactory = RemoteUserInfoFallbackFactory.class)
public interface RemoteUserInfoService {
    @PostMapping("/userInfo/register")
    R<Boolean> register(@RequestBody UserInfo userInfo);

    @PutMapping("/userInfo/updateUserLogin")
    R<Boolean> updateUserLogin(@RequestBody UpdateUserLogin updateUserLogin);

    @GetMapping("/userInfo/info/{username}")
    R<UserInfo> getUserInfo(@PathVariable("username") String username);

    @GetMapping("/userInfo/saveUserBrowseHistory/{skuId}/{userId}")
    R<Void> saveUserBrowseHistory(@PathVariable("skuId") Long skuId, @PathVariable("userId") Long userId);
}
