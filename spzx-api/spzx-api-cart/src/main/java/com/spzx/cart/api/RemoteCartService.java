package com.spzx.cart.api;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.api.factory.RemoteCartFallbackFactory;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 远程购物车服务
 */
@FeignClient(contextId = "remoteCartService", value = ServiceNameConstants.CART_SERVICE,
        fallbackFactory = RemoteCartFallbackFactory.class)
public interface RemoteCartService {
    @GetMapping("/getCartCheckedList/{userId}")
    R<List<CartInfo>> getCartCheckedList(@PathVariable("userId") Long userId);

    @GetMapping("/updateCartPrice/{userId}")
    R<Boolean> updateCartPrice(@PathVariable("userId") Long userId);

    @GetMapping("/deleteCartCheckedList/{userId}")
    R<Boolean> deleteCartCheckedList(@PathVariable("userId") Long userId);
}
