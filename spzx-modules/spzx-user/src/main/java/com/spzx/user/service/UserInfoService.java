package com.spzx.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.domain.UserBrowseHistory;
import com.spzx.user.domain.UserCollect;

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

    /**
     * 会员注册
     *
     * @param userInfo 会员注册信息
     */
    void register(UserInfo userInfo);

    /**
     * 更新用户登录信息
     *
     * @param updateUserLogin 用户登录信息
     * @return 结果
     */
    Boolean updateUserLogin(UpdateUserLogin updateUserLogin);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserInfo selectUserInfoByUsername(String username);

    /* 用户收藏模块 */
    IPage<UserCollect> userCollectList(Page<UserCollect> page);

    Boolean isCollect(Long skuId);

    Boolean collect(Long skuId);

    Boolean cancelCollect(Long skuId);

    /* 用户浏览历史模块 */
    IPage<UserBrowseHistory> userBrowseHistoryList(Page<UserBrowseHistory> pageParam);

    void saveUserBrowseHistory(Long skuId, Long userId);

}
