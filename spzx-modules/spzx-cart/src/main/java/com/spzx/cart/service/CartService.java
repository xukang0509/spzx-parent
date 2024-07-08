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

    /**
     * 删除购物车
     *
     * @param skuId
     */
    void deleteCartBySkuId(Long skuId);

    /**
     * 更新选中商品状态
     *
     * @param skuId     skuId
     * @param isChecked 是否选中：1：选中；0：取消选中
     */
    void checkCart(Long skuId, Integer isChecked);

    /**
     * 更新购物车商品的全选
     *
     * @param isChecked 是否选中：1：选中；0：取消选中
     */
    void allCheckCart(Integer isChecked);

    /**
     * 清空购物车
     */
    void clearCart();


    List<CartInfo> getCartCheckedList(Long userId);
}
