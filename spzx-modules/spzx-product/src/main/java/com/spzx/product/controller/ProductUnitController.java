package com.spzx.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.service.ProductUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品单位Controller
 *
 * @author spzx
 */
@Tag(name = "商品单位接口管理")
@RestController
@RequestMapping("/productUnit")
public class ProductUnitController extends BaseController {
    @Resource
    private ProductUnitService productUnitService;

    @Operation(summary = "获取分页列表")
    @GetMapping("/list")
    public TableDataInfo findPage(
            @Parameter(name = "pageNum", description = "当前页码", required = true)
            @RequestParam(value = "pageNum", defaultValue = "1", required = true)
            Integer pageNum,
            @Parameter(name = "pageSize", description = "每页记录数", required = true)
            @RequestParam(value = "pageSize", defaultValue = "10", required = true)
            Integer pageSize,
            @Parameter(name = "queryInfo", description = "查询对象", required = false)
            ProductUnit productUnit
    ) {
        Page<ProductUnit> pageParam = new Page<>(pageNum, pageSize);
        IPage<ProductUnit> iPage = productUnitService.selectProductUnitPage(pageParam, productUnit);
        return getDataTable(iPage);
    }

    @Operation(summary = "获取商品单位详细信息")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productUnitService.selectProductUnitById(id));
    }

    @Operation(summary = "新增商品单位")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated ProductUnit productUnit) {
        if (!productUnitService.checkUniqueName(productUnit)) {
            return error("新增商品单位'" + productUnit.getName() + "'失败，商品单位名称已存在");
        }
        return toAjax(productUnitService.insertProductUnit(productUnit));
    }

    @Operation(summary = "修改商品单位")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated ProductUnit productUnit) {
        if (!productUnitService.checkUniqueName(productUnit)) {
            return error("更新商品单位'" + productUnit.getName() + "'失败，商品单位名称已存在");
        }
        return toAjax(productUnitService.updateProductUnit(productUnit));
    }

    @Operation(summary = "删除商品单位")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids) {
        return toAjax(productUnitService.deleteProductUnitByIds(ids));
    }

    @Operation(summary = "获取全部单元")
    @GetMapping("/getUnitAll")
    public AjaxResult getAllProductUnits() {
        return success(productUnitService.list());
    }
}
