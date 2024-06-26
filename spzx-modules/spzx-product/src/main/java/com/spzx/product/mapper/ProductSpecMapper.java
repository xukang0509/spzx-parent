package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.ProductSpec;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品规格Mapper接口
 *
 * @author spzx
 */
public interface ProductSpecMapper extends BaseMapper<ProductSpec> {
    /**
     * 查询商品规格列表
     *
     * @param productSpec 商品规格查询条件
     * @return 商品规格集合
     */
    List<ProductSpec> selectProductSpecList(ProductSpec productSpec);

    /**
     * 批量删除商品规格
     *
     * @param ids 商品规格主键集合
     * @return 结果
     */
    int deleteProductSpecByIds(@Param("ids") Long[] ids, @Param("username") String username);
}
