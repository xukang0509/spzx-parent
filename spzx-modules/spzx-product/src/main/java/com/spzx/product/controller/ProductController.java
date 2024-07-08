package com.spzx.product.controller;

import com.github.pagehelper.PageHelper;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.product.api.domain.*;
import com.spzx.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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

    @InnerAuth
    @Operation(summary = "获取销量好的sku")
    @GetMapping("getTopSale")
    public R<List<ProductSku>> getTopSale() {
        return R.ok(productService.getTopSale());
    }

    @InnerAuth
    @Operation(summary = "条件查询SKU")
    @GetMapping("/skuList/{pageNum}/{pageSize}")
    public R<TableDataInfo> skuList(
            @Parameter(name = "pageNum", description = "当前页码", required = true)
            @PathVariable Integer pageNum,
            @Parameter(name = "pageSize", description = "每页记录数", required = true)
            @PathVariable Integer pageSize,
            @Parameter(name = "productQuery", description = "查询对象", required = false)
            @ModelAttribute SkuQuery skuQuery
    ) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductSku> list = productService.selectProductSkuList(skuQuery);
        return R.ok(getDataTable(list));
    }

    @Operation(summary = "获取商品sku信息")
    @InnerAuth
    @GetMapping("/getProductSku/{skuId}")
    public R<ProductSku> getProductSku(@PathVariable Long skuId) {
        return R.ok(productService.getProductSkuBySkuId(skuId));
    }

    @Operation(summary = "获取商品信息")
    @InnerAuth
    @GetMapping("/getProduct/{productId}")
    public R<Product> getProduct(@PathVariable Long productId) {
        return R.ok(productService.getProductById(productId));
    }

    @Operation(summary = "获取商品详细信息")
    @InnerAuth
    @GetMapping("/getProductDetails/{productId}")
    public R<ProductDetails> getProductDetails(@PathVariable Long productId) {
        return R.ok(productService.getProductDetailsByProductId(productId));
    }

    @Operation(summary = "获取sku库存信息")
    @InnerAuth
    @GetMapping("/getSkuStockVo/{skuId}")
    public R<SkuStockVo> getSkuStockVo(@PathVariable Long skuId) {
        return R.ok(productService.getSkuStockVoBySkuId(skuId));
    }

    @Operation(summary = "获取商品sku规则详细信息")
    @InnerAuth
    @GetMapping("/getSkuSpecValue/{productId}")
    public R<Map<String, Long>> getSkuSpecValueMap(@PathVariable Long productId) {
        return R.ok(productService.getSkuSpecValueMapByProductId(productId));
    }

    @Operation(summary = "批量获取商品sku最新价格信息")
    @InnerAuth
    @PostMapping("getSkuPriceList")
    public R<List<SkuPrice>> getSkuPriceList(@RequestBody List<Long> skuIds) {
        return R.ok(productService.getSkuPriceListBySkuIds(skuIds));
    }
}
