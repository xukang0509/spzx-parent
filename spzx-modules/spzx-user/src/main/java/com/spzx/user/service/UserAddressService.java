package com.spzx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.api.domain.UserAddress;

import java.util.List;

/**
 * 用户地址Service接口
 *
 * @author xukang
 * @date 2024-07-02
 */
public interface UserAddressService extends IService<UserAddress> {

    List<UserAddress> selectUserAddressList();

    int insertUserAddress(UserAddress userAddress);

    int updateUserAddress(UserAddress userAddress);
}
