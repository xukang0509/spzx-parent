package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.CategoryBrand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类品牌Mapper接口
 *
 * @author spzx
 */
public interface CategoryBrandMapper extends BaseMapper<CategoryBrand> {
    /**
     * 查询分类品牌列表
     *
     * @param categoryBrand 分类品牌
     * @return 分类品牌集合
     */
    List<CategoryBrand> selectCategoryBrandList(@Param("categoryBrand") CategoryBrand categoryBrand);

    /**
     * 批量删除分类品牌
     *
     * @param ids      分类品牌主键集合
     * @param username 用户
     * @return 结果
     */
    int deleteCategoryBrandByIds(@Param("ids") Long[] ids, @Param("username") String username);
}
