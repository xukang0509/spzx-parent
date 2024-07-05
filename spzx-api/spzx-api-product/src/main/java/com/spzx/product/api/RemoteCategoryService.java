package com.spzx.product.api;

import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.domain.CategoryVo;
import com.spzx.product.api.factory.RemoteCategoryFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 远程商品分类服务
 */
@FeignClient(contextId = "remoteCategoryService", value = ServiceNameConstants.PRODUCT_SERVICE,
        fallbackFactory = RemoteCategoryFallbackFactory.class)
public interface RemoteCategoryService {
    @GetMapping("/category/getOneCategory")
    R<List<CategoryVo>> getOneCategory();

    @GetMapping(value = "/category/getTreeCategory")
    R<List<CategoryVo>> treeCategory();
}
