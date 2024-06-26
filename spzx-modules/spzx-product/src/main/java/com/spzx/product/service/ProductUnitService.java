package com.spzx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.ProductUnit;

/**
 * 商品单位Service接口
 *
 * @author spzx
 */
public interface ProductUnitService extends IService<ProductUnit> {
    /**
     * 查询商品单位分页列表
     *
     * @param pageParam
     * @param productUnit
     * @return
     */
    IPage<ProductUnit> selectProductUnitPage(Page<ProductUnit> pageParam, ProductUnit productUnit);

    /**
     * 查询商品单位
     *
     * @param id 商品单位主键
     * @return 商品单位
     */
    ProductUnit selectProductUnitById(Long id);

    /**
     * 新增商品单位
     *
     * @param productUnit 商品单位
     * @return 结果
     */
    int insertProductUnit(ProductUnit productUnit);

    /**
     * 修改商品单位
     *
     * @param productUnit 商品单位
     * @return 结果
     */
    int updateProductUnit(ProductUnit productUnit);

    /**
     * 批量删除商品单位
     *
     * @param ids 商品单位主键集合
     * @return 结果
     */
    int deleteProductUnitByIds(Long[] ids);
}
