package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.product.domain.Product;
import com.spzx.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


/**
 * 商品 product Controller层
 *
 * @author spzx
 */
@Tag(name = "商品接口管理")
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {
    @Resource
    private ProductService productService;

    @RequiresPermissions("product:product:list")
    @Operation(summary = "查询商品列表")
    @GetMapping("/list")
    public TableDataInfo list(Product product) {
        startPage();
        return getDataTable(productService.selectProductList(product));
    }

    @Log(title = "商品管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("product:product:add")
    @Operation(summary = "新增商品")
    @PostMapping
    public AjaxResult add(@RequestBody Product product) {
        return toAjax(productService.insertProduct(product));
    }

    @RequiresPermissions("product:product:query")
    @Operation(summary = "获取商品详细信息")
    @GetMapping("/{id}")
    public AjaxResult getDetailProduct(@PathVariable("id") Long id) {
        return success(productService.selectProductById(id));
    }

    @Log(title = "商品管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("product:product:edit")
    @Operation(summary = "修改商品")
    @PutMapping
    public AjaxResult edit(@RequestBody Product product) {
        return toAjax(productService.updateProduct(product));
    }

    @Log(title = "商品管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("product:product:remove")
    @Operation(summary = "删除商品")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids) {
        return toAjax(productService.deleteProductsByIds(ids));
    }

    @Operation(summary = "商品审核")
    @GetMapping("/updateAuditStatus/{id}/{auditStatus}")
    public AjaxResult updateAuditStatus(@PathVariable("id") Long id, @PathVariable("auditStatus") Integer auditStatus) {
        productService.updateAuditStatus(id, auditStatus);
        return success();
    }

    @Operation(summary = "更新上下架状态")
    @GetMapping("updateStatus/{id}/{status}")
    public AjaxResult updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        productService.updateStatus(id, status);
        return success();
    }
}
