package com.spzx.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.user.domain.UserAddress;
import com.spzx.user.domain.UserInfo;
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

}
