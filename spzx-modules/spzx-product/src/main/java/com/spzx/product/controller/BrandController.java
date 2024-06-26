package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Brand;
import com.spzx.product.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author spzx
 */
@Tag(name = "品牌接口管理")
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {
    @Resource
    private BrandService brandService;

    @Operation(summary = "查询品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(Brand brand) {
        startPage();
        List<Brand> list = brandService.selectBrandList(brand);
        return getDataTable(list);
    }

    @Operation(summary = "获取品牌详细信息")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(brandService.selectBrandById(id));
    }

    @Operation(summary = "新增品牌")
    @PostMapping
    public AjaxResult addBrand(@RequestBody @Validated Brand brand) {
        if (!brandService.checkUniqueName(brand)) {
            return error("新增品牌'" + brand.getName() + "'失败，分类品牌已存在");
        }
        brand.setCreateBy(SecurityUtils.getUsername());
        brand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(brandService.insertBrand(brand));
    }

    @Operation(summary = "修改品牌")
    @PutMapping
    public AjaxResult editBrand(@RequestBody @Validated Brand brand) {
        if (!brandService.checkUniqueName(brand)) {
            return error("更新品牌'" + brand.getName() + "'失败，分类品牌已存在");
        }
        brand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(brandService.updateBrand(brand));
    }

    @Operation(summary = "删除品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids) {
        return toAjax(brandService.deleteBrandByIds(ids));
    }

    @Operation(summary = "获取所有品牌列表")
    @GetMapping("/getBrandAll")
    public AjaxResult getBrandAll() {
        return success(brandService.selectBrandAll());
    }
}
