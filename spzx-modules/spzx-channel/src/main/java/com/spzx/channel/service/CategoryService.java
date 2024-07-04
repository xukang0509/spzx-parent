package com.spzx.channel.service;

import com.spzx.product.api.domain.CategoryVo;

import java.util.List;

public interface CategoryService {
    List<CategoryVo> getTreeCategory();
}
