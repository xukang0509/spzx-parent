package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.Category;

import java.util.List;

/**
 * 商品分类Service接口
 *
 * @author spzx
 */
public interface CategoryService extends IService<Category> {
    /**
     * 获取分类下拉树列表
     *
     * @param parentId
     * @return
     */
    List<Category> treeSelect(Long parentId);

    /**
     * 根据分类id获取全部本级及上级节点id列表
     *
     * @param id
     * @return
     */
    List<Long> getAllCategoryIdList(Long id);
}
