package com.spzx.channel.controller;

import com.spzx.channel.service.BrandService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "品牌管理")
@RestController
@RequestMapping("brand")
public class BrandController extends BaseController {
    @Resource
    private BrandService brandService;

    @Operation(summary = "获取所有品牌")
    @GetMapping("getBrandAll")
    public AjaxResult getBrandAll() {
        return success(brandService.getBrandAll());
    }
}
