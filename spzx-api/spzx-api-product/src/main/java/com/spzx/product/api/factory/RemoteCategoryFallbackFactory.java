package com.spzx.product.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.domain.CategoryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 远程商品分类服务降级处理
 */
@Component
public class RemoteCategoryFallbackFactory implements FallbackFactory<RemoteCategoryService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteCategoryFallbackFactory.class);

    @Override
    public RemoteCategoryService create(Throwable cause) {
        log.error("商品服务调用失败：{}", cause.getMessage());
        return new RemoteCategoryService() {
            @Override
            public R<List<CategoryVo>> getOneCategory(String source) {
                return R.fail("获取全部一级分类失败：" + cause.getMessage());
            }

            @Override
            public R<List<CategoryVo>> treeCategory(String source) {
                return R.fail("获取分类数据树形展示失败：" + cause.getMessage());
            }
        };
    }
}
