package com.spzx.cart.api.factory;

import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoteCartFallbackFactory implements FallbackFactory<RemoteCartService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteCartFallbackFactory.class);

    @Override
    public RemoteCartService create(Throwable cause) {
        log.error("购物车服务调用失败:{}", cause.getMessage());
        return new RemoteCartService() {
            @Override
            public R<List<CartInfo>> getCartCheckedList(Long userId) {
                return R.fail("查询用户购物车列表中选中商品列表失败：" + cause.getMessage());
            }

            @Override
            public R<Boolean> updateCartPrice(Long userId) {
                return R.fail("更新用户购物车列表价格失败：" + cause.getMessage());
            }

            @Override
            public R<Boolean> deleteCartCheckedList(Long userId) {
                return R.fail("删除用户购物车列表中选中商品列表失败：" + cause.getMessage());
            }
        };
    }
}
