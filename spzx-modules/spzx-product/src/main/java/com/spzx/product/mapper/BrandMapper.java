package com.spzx.product.mapper;

import com.spzx.product.domain.Brand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 品牌mapper接口
 */
public interface BrandMapper {

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
     * 新增保存品牌信息
     *
     * @param brand 品牌信息
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
     * @param ids      需要删除的主键集合
     * @param userName 用户
     * @return 结果
     */
    int deleteBrandByIds(@Param("ids") Long[] ids, @Param("userName") String userName);
}
