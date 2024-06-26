package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
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

    @Operation(summary = "查询商品规格列表")
    @GetMapping("/list")
    public TableDataInfo pageList(ProductSpec productSpec) {
        startPage();
        return getDataTable(productSpecService.selectProductSpecList(productSpec));
    }

    @Operation(summary = "获取商品规格详细信息")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productSpecService.selectProductSpecById(id));
    }

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

    @Operation(summary = "修改商品规格")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated ProductSpec productSpec) {
        productSpec.setUpdateBy(SecurityUtils.getUsername());
        productSpec.setUpdateTime(new Date());
        return toAjax(productSpecService.updateById(productSpec));
    }

    @Operation(summary = "删除商品规格")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids) {
        return toAjax(productSpecService.deleteProductSpecByIds(ids));
    }
}
