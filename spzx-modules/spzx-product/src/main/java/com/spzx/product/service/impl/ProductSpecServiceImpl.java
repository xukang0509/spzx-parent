package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.mapper.ProductSpecMapper;
import com.spzx.product.service.CategoryService;
import com.spzx.product.service.ProductSpecService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品规格业务层处理
 *
 * @author spzx
 */
@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec>
        implements ProductSpecService {
    @Resource
    private ProductSpecMapper productSpecMapper;

    @Resource
    private CategoryService categoryService;

    @Override
    public List<ProductSpec> selectProductSpecList(ProductSpec productSpec) {
        return productSpecMapper.selectProductSpecList(productSpec);
    }

    @Override
    public ProductSpec selectProductSpecById(Long id) {
        ProductSpec productSpec = productSpecMapper.selectById(id);
        List<Long> categoryIdList = categoryService.getAllCategoryIdList(productSpec.getCategoryId());
        productSpec.setCategoryIdList(categoryIdList);
        return productSpec;
    }

    @Override
    public int deleteProductSpecByIds(Long[] ids) {
        return productSpecMapper.deleteProductSpecByIds(ids, SecurityUtils.getUsername());
    }

    @Override
    public List<ProductSpec> selectProductSpecListByCategoryId(Long categoryId) {
        return productSpecMapper.selectList(Wrappers.lambdaQuery(ProductSpec.class)
                .eq(ProductSpec::getCategoryId, categoryId));
    }
}
