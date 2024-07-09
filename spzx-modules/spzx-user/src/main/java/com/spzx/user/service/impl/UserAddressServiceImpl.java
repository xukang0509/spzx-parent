package com.spzx.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.utils.DateUtils;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.mapper.UserAddressMapper;
import com.spzx.user.service.RegionService;
import com.spzx.user.service.UserAddressService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户地址Service业务层处理
 *
 * @author xukang
 * @date 2024-07-02
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {
    @Resource
    private UserAddressMapper userAddressMapper;
    @Resource
    private RegionService regionService;

    @Override
    public List<UserAddress> selectUserAddressList() {
        Long userId = SecurityContextHolder.getUserId();
        return userAddressMapper.selectList(Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getUserId, userId));
    }

    @Override
    public int insertUserAddress(UserAddress userAddress) {
        userAddress.setUserId(SecurityContextHolder.getUserId());
        userAddress.setCreateBy(SecurityContextHolder.getUserName());
        userAddress.setCreateTime(DateUtils.getNowDate());

        String provinceName = regionService.getNameByCode(userAddress.getProvinceCode());
        String cityName = regionService.getNameByCode(userAddress.getCityCode());
        String districtName = regionService.getNameByCode(userAddress.getDistrictCode());
        String fullAddressName = provinceName + cityName + districtName + userAddress.getAddress();
        userAddress.setFullAddress(fullAddressName);

        // 如果是默认地址，那么其它地址更新为非默认地址
        if (userAddress.getIsDefault().intValue() == 1) {
            UserAddress userAddressUp = new UserAddress();
            userAddressUp.setIsDefault(0L);
            userAddressMapper.update(userAddressUp, Wrappers.lambdaQuery(UserAddress.class)
                    .eq(UserAddress::getUserId, userAddress.getUserId()));
        }
        return userAddressMapper.insert(userAddress);
    }

    @Override
    public int updateUserAddress(UserAddress userAddress) {
        userAddress.setUserId(SecurityContextHolder.getUserId());
        userAddress.setUpdateTime(DateUtils.getNowDate());
        userAddress.setUpdateBy(SecurityContextHolder.getUserName());

        String provinceName = regionService.getNameByCode(userAddress.getProvinceCode());
        String cityName = regionService.getNameByCode(userAddress.getCityCode());
        String districtName = regionService.getNameByCode(userAddress.getDistrictCode());
        String fullAddressName = provinceName + cityName + districtName + userAddress.getAddress();
        userAddress.setFullAddress(fullAddressName);

        // 如果是默认地址，那么其它地址更新为非默认地址
        if (userAddress.getIsDefault().intValue() == 1) {
            UserAddress userAddressUp = new UserAddress();
            userAddressUp.setIsDefault(0L);
            userAddressMapper.update(userAddressUp, Wrappers.lambdaQuery(UserAddress.class)
                    .eq(UserAddress::getUserId, userAddress.getUserId()));
        }
        return userAddressMapper.updateById(userAddress);
    }
}
