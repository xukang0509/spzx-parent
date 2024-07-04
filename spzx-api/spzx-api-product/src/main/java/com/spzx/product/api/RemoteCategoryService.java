package com.spzx.product.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.domain.CategoryVo;
import com.spzx.product.api.factory.RemoteCategoryFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 远程商品分类服务
 */
@FeignClient(contextId = "remoteCategoryService", value = ServiceNameConstants.PRODUCT_SERVICE,
        fallbackFactory = RemoteCategoryFallbackFactory.class)
public interface RemoteCategoryService {
    @GetMapping("/category/getOneCategory")
    R<List<CategoryVo>> getOneCategory(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping(value = "/category/getTreeCategory")
    R<List<CategoryVo>> treeCategory(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
