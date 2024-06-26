package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品分类Controller
 *
 * @author spzx
 */
@Tag(name = "商品分类接口管理")
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {
    @Resource
    private CategoryService categoryService;

    @Operation(summary = "获取商品分类下拉树列表")
    @GetMapping("/treeSelect/{id}")
    public AjaxResult treeSelect(@PathVariable("id") Long id) {
        return success(categoryService.treeSelect(id));
    }
}
