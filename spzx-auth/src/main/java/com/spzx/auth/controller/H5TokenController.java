package com.spzx.auth.controller;

import com.spzx.auth.form.RegisterBody;
import com.spzx.auth.service.H5LoginService;
import com.spzx.common.core.domain.R;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * H5 token 控制
 */
@RestController
@RequestMapping("/h5")
public class H5TokenController {
    @Resource
    private H5LoginService h5LoginService;

    @PostMapping("/register")
    public R<?> register(@RequestBody RegisterBody registerBody) {
        // 用户注册
        h5LoginService.register(registerBody);
        return R.ok();
    }
}
