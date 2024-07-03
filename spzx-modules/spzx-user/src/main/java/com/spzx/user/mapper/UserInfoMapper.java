package com.spzx.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.user.domain.UserInfo;

import java.util.List;

/**
 * 会员Mapper接口
 *
 * @author xukang
 * @date 2024-07-02
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员集合
     */
    List<UserInfo> selectUserInfoList(UserInfo userInfo);

}
