package com.spzx.user.controller;

import com.spzx.common.core.utils.poi.ExcelUtil;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.user.domain.UserAddress;
import com.spzx.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 用户地址Controller
 *
 * @author xukang
 * @date 2024-07-02
 */
@Tag(name = "用户地址接口管理")
@RestController
@RequestMapping("/userAddress")
public class UserAddressController extends BaseController {
    @Autowired
    private UserAddressService userAddressService;

    /**
     * 查询用户地址列表
     */
    @Operation(summary = "查询用户地址列表")
    @RequiresPermissions("user:userAddress:list")
    @GetMapping("/list")
    public TableDataInfo list(UserAddress userAddress) {
        startPage();
        List<UserAddress> list = userAddressService.selectUserAddressList(userAddress);
        return getDataTable(list);
    }

    /**
     * 导出用户地址列表
     */
    @Operation(summary = "导出用户地址列表")
    @RequiresPermissions("user:userAddress:export")
    @Log(title = "用户地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserAddress userAddress) {
        List<UserAddress> list = userAddressService.selectUserAddressList(userAddress);
        ExcelUtil<UserAddress> util = new ExcelUtil<UserAddress>(UserAddress.class);
        util.exportExcel(response, list, "用户地址数据");
    }

    /**
     * 获取用户地址详细信息
     */
    @Operation(summary = "获取用户地址详细信息")
    @RequiresPermissions("user:userAddress:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(userAddressService.getById(id));
    }

    /**
     * 新增用户地址
     */
    @Operation(summary = "新增用户地址")
    @RequiresPermissions("user:userAddress:add")
    @Log(title = "用户地址", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserAddress userAddress) {
        return toAjax(userAddressService.save(userAddress));
    }

    /**
     * 修改用户地址
     */
    @Operation(summary = "修改用户地址")
    @RequiresPermissions("user:userAddress:edit")
    @Log(title = "用户地址", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserAddress userAddress) {
        return toAjax(userAddressService.updateById(userAddress));
    }

    /**
     * 删除用户地址
     */
    @Operation(summary = "删除用户地址")
    @RequiresPermissions("user:userAddress:remove")
    @Log(title = "用户地址", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(userAddressService.removeBatchByIds(Arrays.asList(ids)));
    }
}
