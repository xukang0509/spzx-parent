package com.spzx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.domain.UserAddress;

import java.util.List;

/**
 * 用户地址Service接口
 *
 * @author xukang
 * @date 2024-07-02
 */
public interface IUserAddressService extends IService<UserAddress> {

    /**
     * 查询用户地址列表
     *
     * @param userAddress 用户地址
     * @return 用户地址集合
     */
    public List<UserAddress> selectUserAddressList(UserAddress userAddress);

}
