package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.mapper.CategoryBrandMapper;
import com.spzx.product.service.CategoryBrandService;
import com.spzx.product.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 分类品牌业务层处理
 *
 * @author spzx
 */
@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements CategoryBrandService {

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private CategoryService categoryService;

    /**
     * 查询分类品牌列表
     *
     * @param categoryBrand 分类品牌
     * @return 分类品牌集合
     */
    @Override
    public List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand) {
        return categoryBrandMapper.selectCategoryBrandList(categoryBrand);
    }

    /**
     * 查询分类品牌详细信息
     *
     * @param id 分类品牌主键ID
     * @return 分类品牌
     */
    @Override
    public CategoryBrand selectCategoryBrandById(Long id) {
        CategoryBrand categoryBrand = categoryBrandMapper.selectById(id);
        List<Long> categoryIdList = categoryService.getAllCategoryIdList(categoryBrand.getCategoryId());
        categoryBrand.setCategoryIdList(categoryIdList);
        return categoryBrand;
    }

    /**
     * 新增分类品牌
     *
     * @param categoryBrand 分类品牌
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertCategoryBrand(CategoryBrand categoryBrand) {
        Long count = categoryBrandMapper.selectCount(Wrappers.lambdaQuery(CategoryBrand.class)
                .eq(CategoryBrand::getCategoryId, categoryBrand.getCategoryId())
                .eq(CategoryBrand::getBrandId, categoryBrand.getBrandId()));
        if (count > 0) {
            throw new ServiceException("该分类已加该品牌");
        }
        Date date = new Date();
        categoryBrand.setCreateBy(SecurityUtils.getUsername());
        categoryBrand.setCreateTime(date);
        categoryBrand.setUpdateBy(SecurityUtils.getUsername());
        categoryBrand.setUpdateTime(date);
        return categoryBrandMapper.insert(categoryBrand);
    }

    /**
     * 修改分类品牌
     *
     * @param categoryBrand 分类品牌
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateCategoryBrand(CategoryBrand categoryBrand) {
        CategoryBrand originalCategoryBrand = categoryBrandMapper.selectById(categoryBrand.getId());
        if (originalCategoryBrand.getCategoryId().longValue() != categoryBrand.getCategoryId().longValue() ||
                originalCategoryBrand.getBrandId().longValue() != categoryBrand.getBrandId().longValue()) {
            Long count = categoryBrandMapper.selectCount(Wrappers.lambdaQuery(CategoryBrand.class)
                    .eq(CategoryBrand::getCategoryId, categoryBrand.getCategoryId())
                    .eq(CategoryBrand::getBrandId, categoryBrand.getBrandId()));
            if (count > 0) {
                throw new ServiceException("该分类已加该品牌");
            }
        }
        categoryBrand.setUpdateBy(SecurityUtils.getUsername());
        categoryBrand.setUpdateTime(new Date());
        return categoryBrandMapper.updateById(categoryBrand);
    }

    /**
     * 批量删除分类品牌
     *
     * @param ids 分类品牌主键集合
     * @return 结果
     */
    @Override
    public int deleteCategoryBrand(Long[] ids) {
        return categoryBrandMapper.deleteCategoryBrandByIds(ids, SecurityUtils.getUsername());
    }
}
