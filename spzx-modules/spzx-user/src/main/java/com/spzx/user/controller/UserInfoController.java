package com.spzx.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.utils.poi.ExcelUtil;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.domain.UserBrowseHistory;
import com.spzx.user.domain.UserCollect;
import com.spzx.user.domain.UserInfoVo;
import com.spzx.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员Controller
 *
 * @author xukang
 * @date 2024-07-02
 */
@Tag(name = "会员接口管理")
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends BaseController {
    @Resource
    private UserInfoService userInfoService;

    @Operation(summary = "查询会员列表")
    @RequiresPermissions("user:userInfo:list")
    @GetMapping("/list")
    public TableDataInfo list(UserInfo userInfo) {
        startPage();
        List<UserInfo> list = userInfoService.selectUserInfoList(userInfo);
        return getDataTable(list);
    }

    /**
     * 导出会员列表
     */
    @Operation(summary = "导出会员列表")
    @RequiresPermissions("user:userInfo:export")
    @Log(title = "会员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserInfo userInfo) {
        List<UserInfo> list = userInfoService.selectUserInfoList(userInfo);
        ExcelUtil<UserInfo> util = new ExcelUtil<>(UserInfo.class);
        util.exportExcel(response, list, "会员数据");
    }

    @Operation(summary = "获取会员地址")
    @RequiresPermissions("user:info:query")
    @GetMapping("/getUserAddress/{userId}")
    public AjaxResult getUserAddress(@PathVariable("userId") Long userId) {
        List<UserAddress> userAddressList = userInfoService.selectUserAddressList(userId);
        return success(userAddressList);
    }

    @Operation(summary = "会员注册")
    @InnerAuth
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody UserInfo userInfo) {
        userInfoService.register(userInfo);
        return R.ok();
    }

    @Operation(summary = "更新用户登录信息")
    @InnerAuth
    @PutMapping("/updateUserLogin")
    public R<Boolean> updateUserLogin(@RequestBody UpdateUserLogin updateUserLogin) {
        return R.ok(userInfoService.updateUserLogin(updateUserLogin));
    }

    @Operation(summary = "根据用户名获取用户信息")
    @InnerAuth
    @GetMapping("/info/{username}")
    public R<UserInfo> getUserInfo(@PathVariable String username) {
        return R.ok(userInfoService.selectUserInfoByUsername(username));
    }

    @RequiresLogin
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/getLoginUserInfo")
    public AjaxResult getLoginUserInfo() {
        Long userId = SecurityContextHolder.getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, userInfoVo);
        return success(userInfoVo);
    }

    /* 用户收藏模块 */
    @RequiresLogin
    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/userCollectList/{pageNum}/{pageSize}")
    public TableDataInfo userCollectList(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        Page<UserCollect> pageParam = new Page<>(pageNum, pageSize);
        IPage<UserCollect> page = userInfoService.userCollectList(pageParam);
        return getDataTable(page);
    }

    @RequiresLogin
    @Operation(summary = "是否收藏sku")
    @GetMapping("/isCollect/{skuId}")
    public AjaxResult isCollect(@PathVariable Long skuId) {
        return success(userInfoService.isCollect(skuId));
    }

    @RequiresLogin
    @Operation(summary = "收藏sku")
    @GetMapping("/collect/{skuId}")
    public AjaxResult collect(@PathVariable Long skuId) {
        return success(userInfoService.collect(skuId));
    }

    @RequiresLogin
    @Operation(summary = "当前用户取消收藏商品")
    @GetMapping("/cancelCollect/{skuId}")
    public AjaxResult cancelCollect(@PathVariable Long skuId) {
        return success(userInfoService.cancelCollect(skuId));
    }

    /* 用户浏览历史操作 */
    @RequiresLogin
    @Operation(summary = "获取用户浏览历史列表")
    @GetMapping("/userBrowseHistoryList/{pageNum}/{pageSize}")
    public TableDataInfo userBrowseHistoryList(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        Page<UserBrowseHistory> pageParam = new Page<>(pageNum, pageSize);
        IPage<UserBrowseHistory> page = userInfoService.userBrowseHistoryList(pageParam);
        return getDataTable(page);
    }

    @InnerAuth
    @Operation(summary = "记录用户浏览记录")
    @GetMapping("/saveUserBrowseHistory/{skuId}/{userId}")
    public R<Void> saveUserBrowseHistory(@PathVariable Long skuId, @PathVariable Long userId) {
        userInfoService.saveUserBrowseHistory(skuId, userId);
        return R.ok();
    }
}
