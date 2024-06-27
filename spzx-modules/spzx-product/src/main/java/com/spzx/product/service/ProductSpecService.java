package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.ProductSpec;

import java.util.List;

/**
 * 商品规格Service接口
 *
 * @author spzx
 */
public interface ProductSpecService extends IService<ProductSpec> {
    /**
     * 查询商品规格列表
     *
     * @param productSpec 商品规格查询条件
     * @return 商品规格集合
     */
    List<ProductSpec> selectProductSpecList(ProductSpec productSpec);

    /**
     * 根据主键ID获取商品规格
     *
     * @param id 商品规格主键id
     * @return 商品规格
     */
    ProductSpec selectProductSpecById(Long id);

    /**
     * 批量删除商品规格
     *
     * @param ids 商品规格主键集合
     * @return 结果
     */
    int deleteProductSpecByIds(Long[] ids);


    /**
     * 根据分类ID获取商品规格列表
     *
     * @param categoryId 分类ID
     * @return 商品规格集合
     */
    List<ProductSpec> selectProductSpecListByCategoryId(Long categoryId);
}
