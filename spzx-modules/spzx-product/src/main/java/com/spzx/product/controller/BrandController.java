package com.spzx.product.controller;

import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.api.domain.BrandVo;
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

    @RequiresPermissions("product:brand:list")
    @Operation(summary = "查询品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(Brand brand) {
        startPage();
        List<Brand> list = brandService.selectBrandList(brand);
        return getDataTable(list);
    }

    @RequiresPermissions("product:brand:query")
    @Operation(summary = "获取品牌详细信息")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(brandService.selectBrandById(id));
    }

    @Log(title = "品牌管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("product:brand:add")
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

    @Log(title = "品牌管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("product:brand:edit")
    @Operation(summary = "修改品牌")
    @PutMapping
    public AjaxResult editBrand(@RequestBody @Validated Brand brand) {
        if (!brandService.checkUniqueName(brand)) {
            return error("更新品牌'" + brand.getName() + "'失败，分类品牌已存在");
        }
        brand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(brandService.updateBrand(brand));
    }

    @Log(title = "品牌管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("product:brand:remove")
    @Operation(summary = "删除品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids) {
        return toAjax(brandService.deleteBrandByIds(ids));
    }

    @RequiresPermissions("product:brand:query")
    @Operation(summary = "获取所有品牌列表")
    @GetMapping("/getBrandAll")
    public AjaxResult getBrandAll() {
        return success(brandService.selectBrandAll());
    }

    @InnerAuth
    @Operation(summary = "获取所有的品牌")
    @GetMapping("getAllBrandVo")
    public R<List<BrandVo>> getBrandVoList() {
        return R.ok(brandService.selectAllBrandVo());
    }
}
