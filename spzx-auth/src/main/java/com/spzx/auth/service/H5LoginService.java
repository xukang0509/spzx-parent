package com.spzx.auth.service;

import com.spzx.auth.form.RegisterBody;
import com.spzx.common.core.constant.CacheConstants;
import com.spzx.common.core.constant.Constants;
import com.spzx.common.core.constant.UserConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.text.Convert;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.core.utils.ip.IpUtils;
import com.spzx.common.redis.service.RedisService;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.system.api.model.LoginUser;
import com.spzx.user.api.RemoteUserInfoService;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class H5LoginService {
    @Resource
    private RemoteUserInfoService remoteUserInfoService;

    @Resource
    private SysRecordLogService recordLogService;

    @Resource
    private RedisService redisService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SysPasswordService sysPasswordService;

    public void register(RegisterBody registerBody) {
        String username = registerBody.getUsername().trim();
        String password = registerBody.getPassword();
        String code = registerBody.getCode();
        String nickName = registerBody.getNickName();

        if (StringUtils.isAnyBlank(username, password)) {
            throw new ServiceException("用户/密码必须填写");
        }
        if (username.length() != 11) {
            throw new ServiceException("账户长度必须是11个字符");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH ||
                password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }
        if (StringUtils.hasText(code)) {
            throw new ServiceException("验证码必须填写");
        }
        String codeValue = stringRedisTemplate.opsForValue().get("phone:code:" + username);
        if (!Objects.equals(codeValue, code)) {
            throw new ServiceException("验证码不正确");
        }

        // 注册用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPhone(username);
        nickName = StringUtils.hasText(nickName) ? nickName : username;
        userInfo.setNickName(nickName);
        userInfo.setPassword(SecurityUtils.encryptPassword(password));

        R<Boolean> registerRes = remoteUserInfoService.register(userInfo);
        if (R.FAIL == registerRes.getCode()) {
            throw new ServiceException(registerRes.getMsg());
        }
        recordLogService.recordLogininfor(username, Constants.REGISTER, "注册成功");
    }

    public LoginUser login(String username, String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户名/密码必须填写");
            throw new ServiceException("用户名/密码必须填写");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH ||
                password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "密码长度必须在5到20个字符之间");
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }
        if (username.length() != 11) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户名长度必须是11个字符");
            throw new ServiceException("用户名长度必须是11个字符");
        }
        // IP黑名单校验
        String blackStr = Convert.toStr(redisService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKIPLIST));
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "很遗憾，访问IP已被列入系统黑名单");
            throw new ServiceException("很遗憾，访问IP已被列入系统黑名单");
        }
        // 查询用户信息
        R<UserInfo> userInfoRes = remoteUserInfoService.getUserInfo(username);
        if (StringUtils.isNull(userInfoRes) || StringUtils.isNull(userInfoRes.getData())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "登录用户不存在");
            throw new ServiceException("登录用户：" + username + " 不存在");
        }
        if (R.FAIL == userInfoRes.getCode()) {
            throw new ServiceException(userInfoRes.getMsg());
        }

        UserInfo userInfo = userInfoRes.getData();
        LoginUser loginUser = new LoginUser();
        loginUser.setUserid(userInfo.getId());
        loginUser.setUsername(userInfo.getUsername());
        loginUser.setPassword(userInfo.getPassword());
        loginUser.setStatus(userInfo.getStatus().toString());
        if ("0".equals(loginUser.getStatus())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户已停用，请联系管理员");
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        sysPasswordService.validate(loginUser, password);
        recordLogService.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");

        // 更新登录信息
        UpdateUserLogin updateUserLogin = new UpdateUserLogin();
        updateUserLogin.setUserId(userInfo.getId());
        updateUserLogin.setLastLoginIp(IpUtils.getIpAddr());
        updateUserLogin.setLastLoginTime(new Date());
        remoteUserInfoService.updateUserLogin(updateUserLogin);

        return loginUser;
    }
}
