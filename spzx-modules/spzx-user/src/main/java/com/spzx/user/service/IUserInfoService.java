package com.spzx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.domain.UserInfo;

import java.util.List;

/**
 * 会员Service接口
 *
 * @author xukang
 * @date 2024-07-02
 */
public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

}
