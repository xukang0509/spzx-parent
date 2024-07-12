package com.spzx.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.domain.UserBrowseHistory;
import com.spzx.user.domain.UserCollect;
import com.spzx.user.mapper.UserBrowseHistoryMapper;
import com.spzx.user.mapper.UserCollectMapper;
import com.spzx.user.mapper.UserInfoMapper;
import com.spzx.user.service.UserAddressService;
import com.spzx.user.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    @Resource
    private UserCollectMapper userCollectMapper;
    @Resource
    private RemoteProductService remoteProductService;
    @Resource
    private UserBrowseHistoryMapper userBrowseHistoryMapper;

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

    /* 用户收藏模块 */
    // 获取用户收藏列表
    @Override
    public IPage<UserCollect> userCollectList(Page<UserCollect> page) {
        Long userId = SecurityContextHolder.getUserId();
        LambdaQueryWrapper<UserCollect> queryWrapper = Wrappers.lambdaQuery(UserCollect.class)
                .eq(UserCollect::getUserId, userId)
                .orderByDesc(UserCollect::getCreateTime);
        Page<UserCollect> userCollectPage = userCollectMapper.selectPage(page, queryWrapper);
        userCollectPage.getRecords().forEach(userCollect -> {
            R<ProductSku> productSkuRes = remoteProductService.getProductSku(userCollect.getSkuId());
            if (R.FAIL == productSkuRes.getCode()) {
                throw new ServiceException(productSkuRes.getMsg());
            }
            userCollect.setSkuName(productSkuRes.getData().getSkuName());
            userCollect.setThumbImg(productSkuRes.getData().getThumbImg());
            userCollect.setSalePrice(productSkuRes.getData().getSalePrice());
        });
        return userCollectPage;
    }

    // 是否收藏sku
    @Override
    public Boolean isCollect(Long skuId) {
        Long userId = SecurityContextHolder.getUserId();
        Long count = userCollectMapper.selectCount(Wrappers.lambdaQuery(UserCollect.class)
                .eq(UserCollect::getUserId, userId)
                .eq(UserCollect::getSkuId, skuId));
        return count > 0;
    }

    // 收藏sku
    @Override
    public Boolean collect(Long skuId) {
        UserCollect userCollect = new UserCollect();
        userCollect.setUserId(SecurityContextHolder.getUserId());
        userCollect.setSkuId(skuId);
        userCollect.setCreateTime(new Date());
        return userCollectMapper.insert(userCollect) > 0;
    }

    // 当前用户取消收藏商品
    @Override
    public Boolean cancelCollect(Long skuId) {
        return userCollectMapper.delete(Wrappers.lambdaQuery(UserCollect.class)
                .eq(UserCollect::getUserId, SecurityContextHolder.getUserId())
                .eq(UserCollect::getSkuId, skuId)) > 0;
    }

    /* 用户浏览历史模块 */

    // 获取用户浏览历史列表
    @Override
    public IPage<UserBrowseHistory> userBrowseHistoryList(Page<UserBrowseHistory> pageParam) {
        LambdaQueryWrapper<UserBrowseHistory> queryWrapper = Wrappers.lambdaQuery(UserBrowseHistory.class)
                .eq(UserBrowseHistory::getUserId, SecurityContextHolder.getUserId())
                .orderByDesc(UserBrowseHistory::getUpdateTime);
        Page<UserBrowseHistory> historyPage = userBrowseHistoryMapper.selectPage(pageParam, queryWrapper);
        historyPage.getRecords().forEach(userBrowseHistory -> {
            R<ProductSku> productSkuRes = remoteProductService.getProductSku(userBrowseHistory.getSkuId());
            if (R.FAIL == productSkuRes.getCode()) {
                throw new ServiceException(productSkuRes.getMsg());
            }
            userBrowseHistory.setSkuName(productSkuRes.getData().getSkuName());
            userBrowseHistory.setThumbImg(productSkuRes.getData().getThumbImg());
            userBrowseHistory.setSalePrice(productSkuRes.getData().getSalePrice());
        });
        return historyPage;
    }

    // 记录用户浏览记录
    @Async
    @Override
    public void saveUserBrowseHistory(Long skuId, Long userId) {
        UserBrowseHistory userBrowseHistory = userBrowseHistoryMapper.selectOne(Wrappers.lambdaQuery(UserBrowseHistory.class)
                .eq(UserBrowseHistory::getUserId, userId)
                .eq(UserBrowseHistory::getSkuId, skuId));
        if (userBrowseHistory == null) {
            userBrowseHistory = new UserBrowseHistory();
            userBrowseHistory.setUserId(userId);
            userBrowseHistory.setSkuId(skuId);
            userBrowseHistory.setCreateTime(new Date());
            userBrowseHistory.setUpdateTime(new Date());
            userBrowseHistoryMapper.insert(userBrowseHistory);
            return;
        }
        userBrowseHistory.setUpdateTime(new Date());
        userBrowseHistoryMapper.updateById(userBrowseHistory);
    }
}
