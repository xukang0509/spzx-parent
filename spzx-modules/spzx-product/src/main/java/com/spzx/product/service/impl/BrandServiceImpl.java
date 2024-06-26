package com.spzx.product.service.impl;

import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Brand;
import com.spzx.product.mapper.BrandMapper;
import com.spzx.product.service.BrandService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 品牌service业务层处理
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Resource
    private BrandMapper brandMapper;

    /**
     * 查询分类品牌列表
     *
     * @param brand 分类品牌
     * @return 分类品牌合集
     */
    @Override
    public List<Brand> selectBrandList(Brand brand) {
        return brandMapper.selectBrandList(brand);
    }

    /**
     * 根据id查询品牌
     *
     * @param id 品牌主键
     * @return 品牌
     */
    @Override
    public Brand selectBrandById(Long id) {
        return brandMapper.selectBrandById(id);
    }

    /**
     * 新增保存品牌信息
     *
     * @param brand 品牌信息
     * @return 结果
     */
    @Override
    public int insertBrand(Brand brand) {
        return brandMapper.insertBrand(brand);
    }

    /**
     * 修改品牌
     *
     * @param brand 品牌
     * @return 结果
     */
    @Override
    public int updateBrand(Brand brand) {
        return brandMapper.updateBrand(brand);
    }

    /**
     * 批量删除品牌
     *
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    @Override
    public int deleteBrandByIds(Long[] ids) {
        return brandMapper.deleteBrandByIds(ids, SecurityUtils.getUsername());
    }

    /**
     * 获取所有品牌
     *
     * @return 品牌合集
     */
    @Override
    public List<Brand> selectBrandAll() {
        return brandMapper.selectBrandList(null);
    }
}
