package com.spzx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.domain.UserAddress;
import com.spzx.user.domain.UserInfo;

import java.util.List;

/**
 * 会员Service接口
 *
 * @author xukang
 * @date 2024-07-02
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员集合
     */
    List<UserInfo> selectUserInfoList(UserInfo userInfo);

    /**
     * 根据会员id获取会员地址集合
     *
     * @param userId 会员id
     * @return 会员地址集合
     */
    List<UserAddress> selectUserAddressList(Long userId);
}