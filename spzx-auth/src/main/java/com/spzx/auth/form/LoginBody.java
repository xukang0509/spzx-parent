package com.spzx.auth.form;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户登录对象
 *
 * @author spzx
 */
@Setter
@Getter
public class LoginBody {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

}
