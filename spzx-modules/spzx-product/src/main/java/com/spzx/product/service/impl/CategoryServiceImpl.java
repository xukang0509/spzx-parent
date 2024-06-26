package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.Category;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类service业务层处理
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> treeSelect(Long parentId) {
        List<Category> categoryList = this.list(Wrappers.lambdaQuery(Category.class).eq(Category::getParentId, parentId));
        if (!CollectionUtils.isEmpty(categoryList)) {
            categoryList.forEach(category -> {
                Long count = categoryMapper.selectCount(Wrappers.lambdaQuery(Category.class).eq(Category::getParentId, category.getId()));
                if (count > 0) {
                    category.setHasChildren(true);
                } else {
                    category.setHasChildren(false);
                }
            });
        }
        return categoryList;
    }

    @Override
    public List<Long> getAllCategoryIdList(Long id) {
        List<Long> categoryIds = new ArrayList<>();
        List<Category> parentCategoryList = getParentCategory(id, new ArrayList<>());
        for (int index = parentCategoryList.size() - 1; index >= 0; index--) {
            categoryIds.add(parentCategoryList.get(index).getId());
        }
        return categoryIds;
    }

    private List<Category> getParentCategory(Long id, List<Category> categoryList) {
        if (id <= 0) return categoryList;
        Category category = categoryMapper.selectOne(Wrappers.lambdaQuery(Category.class)
                .eq(Category::getId, id)
                .select(Category::getId, Category::getParentId));
        categoryList.add(category);
        return getParentCategory(category.getParentId(), categoryList);
    }
}
