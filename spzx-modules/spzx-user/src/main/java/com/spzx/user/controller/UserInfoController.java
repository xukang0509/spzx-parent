package com.spzx.user.controller;

import com.spzx.common.core.domain.R;
import com.spzx.common.core.utils.poi.ExcelUtil;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.domain.UserAddress;
import com.spzx.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
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
}
