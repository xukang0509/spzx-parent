package com.spzx.channel.controller;

import com.spzx.channel.service.CategoryService;
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
@Tag(name = "首页管理")
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {
    @Resource
    private CategoryService categoryService;

    @Operation(summary = "获取商品分类树形数据")
    @GetMapping("/tree")
    public AjaxResult tree() {
        return success(categoryService.getTreeCategory());
    }
}
