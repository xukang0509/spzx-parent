package com.spzx.user.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.user.domain.UserAddress;
import com.spzx.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
    @Resource
    private UserAddressService userAddressService;

    /**
     * 查询用户地址列表
     */
    @Operation(summary = "查询用户地址列表")
    @RequiresLogin
    @GetMapping("/list")
    public AjaxResult list() {
        return success(userAddressService.selectUserAddressList());
    }

    /**
     * 新增用户地址
     */
    @Operation(summary = "新增用户地址")
    @Log(title = "用户地址", businessType = BusinessType.INSERT)
    @RequiresLogin
    @PostMapping
    public AjaxResult add(@RequestBody UserAddress userAddress) {
        return toAjax(userAddressService.insertUserAddress(userAddress));
    }

    /**
     * 修改用户地址
     */
    @Operation(summary = "修改用户地址")
    @Log(title = "用户地址", businessType = BusinessType.UPDATE)
    @RequiresLogin
    @PutMapping
    public AjaxResult edit(@RequestBody UserAddress userAddress) {
        return toAjax(userAddressService.updateUserAddress(userAddress));
    }

    /**
     * 删除用户地址
     */
    @Operation(summary = "删除用户地址")
    @Log(title = "用户地址", businessType = BusinessType.DELETE)
    @RequiresLogin
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(userAddressService.removeById(id));
    }
}
