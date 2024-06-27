package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.CategoryBrand;

import java.util.List;

/**
 * 分类品牌Service接口
 *
 * @author spzx
 */
public interface CategoryBrandService extends IService<CategoryBrand> {
    /**
     * 查询分类品牌列表
     *
     * @param categoryBrand 分类品牌
     * @return 分类品牌集合
     */
    List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand);

    /**
     * 查询分类品牌详细信息
     *
     * @param id 分类品牌主键ID
     * @return 分类品牌
     */
    CategoryBrand selectCategoryBrandById(Long id);

    /**
     * 新增分类品牌
     *
     * @param categoryBrand 分类品牌
     * @return 结果
     */
    int insertCategoryBrand(CategoryBrand categoryBrand);

    /**
     * 修改分类品牌
     *
     * @param categoryBrand 分类品牌
     * @return 结果
     */
    int updateCategoryBrand(CategoryBrand categoryBrand);

    /**
     * 批量删除分类品牌
     *
     * @param ids 分类品牌主键集合
     * @return 结果
     */
    int deleteCategoryBrand(Long[] ids);

    /**
     * 根据分类ID获取品牌列表
     *
     * @param categoryId 分类ID
     * @return 品牌列表
     */
    List<Brand> selectBrandListByCategoryId(Long categoryId);
}
