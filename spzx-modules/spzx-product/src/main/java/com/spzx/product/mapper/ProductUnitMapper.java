package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.product.domain.ProductUnit;
import org.apache.ibatis.annotations.Param;

/**
 * 商品单位Mapper接口
 *
 * @author spzx
 */
public interface ProductUnitMapper extends BaseMapper<ProductUnit> {
    /**
     * 查询商品单位分页列表
     *
     * @param pageParam
     * @param productUnit
     * @return
     */
    IPage<ProductUnit> selectProductUnitPage(@Param("page") Page<ProductUnit> pageParam,
                                             @Param("query") ProductUnit productUnit);
}
