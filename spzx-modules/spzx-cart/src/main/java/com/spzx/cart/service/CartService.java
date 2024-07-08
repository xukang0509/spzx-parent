package com.spzx.cart.service;

import com.spzx.cart.api.domain.CartInfo;

import java.util.List;

public interface CartService {
    /**
     * 添加购物车
     *
     * @param skuId  skuId
     * @param skuNum sku数量
     */
    void addToCart(Long skuId, Integer skuNum);

    /**
     * 根据用户Id获取购物车列表
     *
     * @return
     */
    List<CartInfo> getCartList();
}
