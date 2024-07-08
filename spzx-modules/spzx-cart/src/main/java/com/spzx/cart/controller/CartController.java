package com.spzx.cart.controller;

import com.spzx.cart.service.CartService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.RequiresLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "购物车接口")
@RestController
@RequestMapping
public class CartController extends BaseController {
    @Resource
    private CartService cartService;

    @Operation(summary = "添加购物车")
    @RequiresLogin
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public AjaxResult addToCart(
            @Parameter(name = "skuId", description = "商品skuId", required = true)
            @PathVariable("skuId") Long skuId,
            @Parameter(name = "skuNum", description = "数量", required = true)
            @PathVariable("skuNum") Integer skuNum
    ) {
        cartService.addToCart(skuId, skuNum);
        return success();
    }

    @Operation(summary = "获取购物车列表")
    @RequiresLogin
    @GetMapping("cartList")
    public AjaxResult cartList() {
        return success(cartService.getCartList());
    }
}
