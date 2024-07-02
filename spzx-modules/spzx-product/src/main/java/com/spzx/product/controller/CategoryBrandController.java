package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.service.CategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类品牌Controller接口
 *
 * @author spzx
 */
@Tag(name = "分类品牌接口管理")
@RestController
@RequestMapping("/categoryBrand")
public class CategoryBrandController extends BaseController {
    @Resource
    private CategoryBrandService categoryBrandService;

    @RequiresPermissions("product:categoryBrand:list")
    @Operation(summary = "查询分类品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(CategoryBrand categoryBrand) {
        startPage();
        List<CategoryBrand> list = categoryBrandService.selectCategoryBrandList(categoryBrand);
        return getDataTable(list);
    }

    @RequiresPermissions("product:categoryBrand:query")
    @Operation(summary = "获取分类品牌详细信息")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(categoryBrandService.selectCategoryBrandById(id));
    }

    @RequiresPermissions("product:categoryBrand:add")
    @Operation(summary = "新增分类品牌")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated CategoryBrand categoryBrand) {
        return toAjax(categoryBrandService.insertCategoryBrand(categoryBrand));
    }

    @RequiresPermissions("product:categoryBrand:edit")
    @Operation(summary = "修改分类品牌")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated CategoryBrand categoryBrand) {
        return toAjax(categoryBrandService.updateCategoryBrand(categoryBrand));
    }

    @RequiresPermissions("product:categoryBrand:remove")
    @Operation(summary = "删除分类品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult delete(@PathVariable("ids") Long[] ids) {
        return toAjax(categoryBrandService.deleteCategoryBrand(ids));
    }

    @Operation(summary = "根据分类ID获取品牌列表")
    @GetMapping("/brandList/{categoryId}")
    public AjaxResult getBrandListByCategoryId(@PathVariable("categoryId") Long categoryId) {
        return success(categoryBrandService.selectBrandListByCategoryId(categoryId));
    }
}
