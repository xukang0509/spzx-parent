package com.spzx.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.mapper.UserInfoMapper;
import com.spzx.user.service.UserAddressService;
import com.spzx.user.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员Service业务层处理
 *
 * @author xukang
 * @date 2024-07-02
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserAddressService userAddressService;

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员
     */
    @Override
    public List<UserInfo> selectUserInfoList(UserInfo userInfo) {
        return userInfoMapper.selectUserInfoList(userInfo);
    }

    /**
     * 根据会员id获取会员地址集合
     *
     * @param userId 会员id
     * @return 会员地址集合
     */
    @Override
    public List<UserAddress> selectUserAddressList(Long userId) {
        return userAddressService.list(Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getUserId, userId));
    }

    /**
     * 会员注册
     *
     * @param userInfo 会员注册信息
     */
    @Override
    public void register(UserInfo userInfo) {
        Long count = userInfoMapper.selectCount(Wrappers.lambdaQuery(UserInfo.class)
                .eq(UserInfo::getUsername, userInfo.getUsername()));
        if (count > 0) {
            throw new ServiceException("会员手机已经存在");
        }
        userInfo.setStatus(1);
        userInfo.setSex(0);
        userInfo.setAvatar("http://192.168.10.102:10001/spzx/2024/07/06/7a6ce31e8d53431a79c790d8561b3b6b_20240706103404A001.jpg");

        userInfoMapper.insert(userInfo);
    }

    @Override
    public Boolean updateUserLogin(UpdateUserLogin updateUserLogin) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(updateUserLogin.getUserId());
        userInfo.setLastLoginIp(updateUserLogin.getLastLoginIp());
        userInfo.setLastLoginTime(updateUserLogin.getLastLoginTime());
        userInfoMapper.updateById(userInfo);
        return true;
    }

    @Override
    public UserInfo selectUserInfoByUsername(String username) {
        return userInfoMapper.selectOne(Wrappers.lambdaQuery(UserInfo.class)
                .eq(UserInfo::getUsername, username));
    }

}
