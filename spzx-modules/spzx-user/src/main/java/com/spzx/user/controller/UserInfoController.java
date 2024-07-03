package com.spzx.user.controller;

import com.spzx.common.core.utils.poi.ExcelUtil;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.user.domain.UserInfo;
import com.spzx.user.service.IUserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 查询会员列表
     */
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
        ExcelUtil<UserInfo> util = new ExcelUtil<UserInfo>(UserInfo.class);
        util.exportExcel(response, list, "会员数据");
    }

    /**
     * 获取会员详细信息
     */
    @Operation(summary = "获取会员详细信息")
    @RequiresPermissions("user:userInfo:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(userInfoService.getById(id));
    }

    /**
     * 新增会员
     */
    @Operation(summary = "新增会员")
    @RequiresPermissions("user:userInfo:add")
    @Log(title = "会员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserInfo userInfo) {
        return toAjax(userInfoService.save(userInfo));
    }

    /**
     * 修改会员
     */
    @Operation(summary = "修改会员")
    @RequiresPermissions("user:userInfo:edit")
    @Log(title = "会员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserInfo userInfo) {
        return toAjax(userInfoService.updateById(userInfo));
    }

    /**
     * 删除会员
     */
    @Operation(summary = "删除会员")
    @RequiresPermissions("user:userInfo:remove")
    @Log(title = "会员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(userInfoService.removeBatchByIds(Arrays.asList(ids)));
    }
}
