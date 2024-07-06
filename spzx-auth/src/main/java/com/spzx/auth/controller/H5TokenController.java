package com.spzx.auth.controller;

import com.spzx.auth.form.LoginBody;
import com.spzx.auth.form.RegisterBody;
import com.spzx.auth.service.H5LoginService;
import com.spzx.auth.service.SysLoginService;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.utils.JwtUtils;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.security.auth.AuthUtil;
import com.spzx.common.security.service.TokenService;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.system.api.model.LoginUser;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * H5 token 控制
 */
@RestController
@RequestMapping("/h5")
public class H5TokenController {
    @Resource
    private H5LoginService h5LoginService;

    @Resource
    private TokenService tokenService;

    @Resource
    private SysLoginService sysLoginService;

    @PostMapping("/register")
    public R<?> register(@RequestBody RegisterBody registerBody) {
        // 用户注册
        h5LoginService.register(registerBody);
        return R.ok();
    }

    @PostMapping("/login")
    public R<?> login(@RequestBody LoginBody loginBody) {
        // 用户登录
        LoginUser loginUser = h5LoginService.login(loginBody.getUsername(), loginBody.getPassword());
        // 获取登录token
        return R.ok(tokenService.createToken(loginUser));
    }

    @DeleteMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String userName = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出登录日志
            sysLoginService.logout(userName);
        }
        return R.ok();
    }
}
