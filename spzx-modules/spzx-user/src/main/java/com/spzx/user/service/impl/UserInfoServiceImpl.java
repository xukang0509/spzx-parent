package com.spzx.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.user.domain.UserInfo;
import com.spzx.user.mapper.UserInfoMapper;
import com.spzx.user.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员Service业务层处理
 *
 * @author xukang
 * @date 2024-07-02
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

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

}
