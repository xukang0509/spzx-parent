package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.mapper.ProductUnitMapper;
import com.spzx.product.service.ProductUnitService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

/**
 * 商品单位Service业务层处理
 *
 * @author spzx
 */
@Service
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper, ProductUnit> implements ProductUnitService {
    @Resource
    private ProductUnitMapper productUnitMapper;

    /**
     * 查询商品单位分页列表
     *
     * @param pageParam
     * @param productUnit
     * @return
     */
    @Override
    public IPage<ProductUnit> selectProductUnitPage(Page<ProductUnit> pageParam, ProductUnit productUnit) {
        return productUnitMapper.selectProductUnitPage(pageParam, productUnit);
    }

    /**
     * 查询商品单位
     *
     * @param id 商品单位主键
     * @return 商品单位
     */
    @Override
    public ProductUnit selectProductUnitById(Long id) {
        return this.getById(id);
    }

    /**
     * 新增商品单位
     *
     * @param productUnit 商品单位
     * @return 结果
     */
    @Override
    public int insertProductUnit(ProductUnit productUnit) {
        Date date = new Date();
        productUnit.setCreateBy(SecurityUtils.getUsername());
        productUnit.setUpdateBy(SecurityUtils.getUsername());
        productUnit.setCreateTime(date);
        productUnit.setUpdateTime(date);
        return this.save(productUnit) ? 1 : 0;
    }

    /**
     * 修改商品单位
     *
     * @param productUnit 商品单位
     * @return 结果
     */
    @Override
    public int updateProductUnit(ProductUnit productUnit) {
        productUnit.setUpdateBy(SecurityUtils.getUsername());
        productUnit.setUpdateTime(new Date());
        return this.updateById(productUnit) ? 1 : 0;
    }

    /**
     * 批量删除商品单位
     *
     * @param ids 商品单位主键集合
     * @return 结果
     */
    @Override
    public int deleteProductUnitByIds(Long[] ids) {
        return this.removeBatchByIds(Arrays.asList(ids)) ? 1 : 0;
    }
}