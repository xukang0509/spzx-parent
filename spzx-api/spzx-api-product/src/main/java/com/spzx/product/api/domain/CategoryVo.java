package com.spzx.product.api.domain;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    /**
     * 分类id
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 图标地址
     */
    private String imageUrl;

    /**
     * 上级分类id
     */
    private Long parentId;

    /**
     * 子节点列表
     */
    private List<CategoryVo> children;
}
