package com.spzx.user.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.user.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地区信息Controller
 *
 * @author xukang
 * @date 2024-07-03
 */
@Tag(name = "地区信息接口管理")
@RestController
@RequestMapping("/region")
public class RegionController extends BaseController {
    @Resource
    private RegionService regionService;

    @Operation(summary = "地区级联查询")
    @GetMapping("/treeSelect/{parentCode}")
    public AjaxResult treeSelect(@PathVariable("parentCode") String parentCode) {
        return success(regionService.treeSelect(parentCode));
    }
}
