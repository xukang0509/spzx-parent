package com.spzx.product.service;


import com.spzx.product.domain.Brand;

import java.util.List;

/**
 * 品牌service接口
 */
public interface BrandService {
    /**
     * 查询分类品牌列表
     *
     * @param brand 分类品牌
     * @return 分类品牌合集
     */
    List<Brand> selectBrandList(Brand brand);

    /**
     * 根据id查询品牌
     *
     * @param id 品牌主键
     * @return 品牌
     */
    Brand selectBrandById(Long id);

    /**
     * 新增品牌
     *
     * @param brand 品牌
     * @return 结果
     */
    int insertBrand(Brand brand);

    /**
     * 修改品牌
     *
     * @param brand 品牌
     * @return 结果
     */
    int updateBrand(Brand brand);

    /**
     * 批量删除品牌
     *
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    int deleteBrandByIds(Long[] ids);

    /**
     * 获取所有品牌
     *
     * @return 品牌合集
     */
    List<Brand> selectBrandAll();
}
