package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
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

    @Operation(summary = "查询分类品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(CategoryBrand categoryBrand) {
        startPage();
        List<CategoryBrand> list = categoryBrandService.selectCategoryBrandList(categoryBrand);
        return getDataTable(list);
    }

    @Operation(summary = "获取分类品牌详细信息")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(categoryBrandService.selectCategoryBrandById(id));
    }

    @Operation(summary = "新增分类品牌")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated CategoryBrand categoryBrand) {
        return toAjax(categoryBrandService.insertCategoryBrand(categoryBrand));
    }

    @Operation(summary = "修改分类品牌")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated CategoryBrand categoryBrand) {
        return toAjax(categoryBrandService.updateCategoryBrand(categoryBrand));
    }

    @Operation(summary = "删除分类品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult delete(@PathVariable("ids") Long[] ids) {
        return toAjax(categoryBrandService.deleteCategoryBrand(ids));
    }
}
