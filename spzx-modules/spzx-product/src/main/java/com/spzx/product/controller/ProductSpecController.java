package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.service.ProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 商品规格Controller
 *
 * @author spzx
 */
@Tag(name = "商品规格接口管理")
@RestController
@RequestMapping("/productSpec")
public class ProductSpecController extends BaseController {

    @Resource
    private ProductSpecService productSpecService;

    @RequiresPermissions("product:productSpec:list")
    @Operation(summary = "查询商品规格列表")
    @GetMapping("/list")
    public TableDataInfo pageList(ProductSpec productSpec) {
        startPage();
        return getDataTable(productSpecService.selectProductSpecList(productSpec));
    }

    @RequiresPermissions("product:productSpec:query")
    @Operation(summary = "获取商品规格详细信息")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productSpecService.selectProductSpecById(id));
    }

    @Log(title = "商品规格管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("product:productSpec:add")
    @Operation(summary = "新增商品规格")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated ProductSpec productSpec) {
        Date date = new Date();
        productSpec.setCreateBy(SecurityUtils.getUsername());
        productSpec.setCreateTime(date);
        productSpec.setUpdateBy(SecurityUtils.getUsername());
        productSpec.setUpdateTime(date);
        return toAjax(productSpecService.save(productSpec));
    }

    @Log(title = "商品规格管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("product:productSpec:edit")
    @Operation(summary = "修改商品规格")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated ProductSpec productSpec) {
        productSpec.setUpdateBy(SecurityUtils.getUsername());
        productSpec.setUpdateTime(new Date());
        return toAjax(productSpecService.updateById(productSpec));
    }

    @Log(title = "商品规格管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("product:productSpec:remove")
    @Operation(summary = "删除商品规格")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids) {
        return toAjax(productSpecService.deleteProductSpecByIds(ids));
    }

    @Operation(summary = "根据分类ID获取商品规格列表")
    @GetMapping("/productSpecList/{categoryId}")
    public AjaxResult getProductSpecListByCategoryId(@PathVariable("categoryId") Long categoryId) {
        return success(productSpecService.selectProductSpecListByCategoryId(categoryId));
    }
}
