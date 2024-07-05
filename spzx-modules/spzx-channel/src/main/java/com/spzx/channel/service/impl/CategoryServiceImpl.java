package com.spzx.channel.service.impl;

import com.spzx.channel.service.CategoryService;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.domain.CategoryVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private RemoteCategoryService remoteCategoryService;

    @Override
    public List<CategoryVo> getTreeCategory() {
        R<List<CategoryVo>> treedCategoryRes = remoteCategoryService.treeCategory();
        if (R.FAIL == treedCategoryRes.getCode()) {
            throw new ServiceException(treedCategoryRes.getMsg());
        }
        return treedCategoryRes.getData();
    }
}
