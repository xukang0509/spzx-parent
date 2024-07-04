package com.spzx.product.controller;

import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.product.api.domain.CategoryVo;
import com.spzx.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @RequiresPermissions("product:category:list")
    @Operation(summary = "获取商品分类下拉树列表")
    @GetMapping("/treeSelect/{id}")
    public AjaxResult treeSelect(@PathVariable("id") Long id) {
        return success(categoryService.treeSelect(id));
    }

    @Log(title = "商品分类管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("product:category:export")
    @Operation(summary = "商品分类导出")
    @PostMapping("/export")
    public void export(HttpServletResponse response) {
        categoryService.exportCategory(response);
    }

    @Log(title = "商品分类管理", businessType = BusinessType.IMPORT)
    @RequiresPermissions("product:category:import")
    @Operation(summary = "商品分类导入")
    @PostMapping("/import")
    public AjaxResult importData(@RequestParam MultipartFile file) {
        try {
            categoryService.importCategory(file);
            return success("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error("导入失败");
    }

    @Operation(summary = "下载模版")
    @PostMapping("/importTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        categoryService.downloadTemplate(response);
    }

    @InnerAuth
    @Operation(summary = "获取商品一级分类")
    @GetMapping(value = "/getOneCategory")
    public R<List<CategoryVo>> getOneCategory() {
        return R.ok(categoryService.getOneCategory());
    }

    @InnerAuth
    @Operation(summary = "分类数据树形展示")
    @GetMapping(value = "/getTreeCategory")
    public R<List<CategoryVo>> treeCategory() {
        return R.ok(categoryService.treeCategory());
    }
}
