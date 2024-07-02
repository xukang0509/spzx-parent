package com.spzx.product.handler;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.spzx.product.domain.Category;
import com.spzx.product.service.CategoryService;
import com.spzx.product.vo.CategoryExcelVo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来解析分类数据的excel文件的监听器
 */
public class CategoryExcelListener extends AnalysisEventListener<CategoryExcelVo> {
    // 集合中的元素个数等于100时，批量保存
    private int limit = 100;

    private CategoryService categoryService;
    private List<Category> categoryList = new ArrayList<>();

    public CategoryExcelListener(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 每行数据回调异一次
    @Override
    public void invoke(CategoryExcelVo categoryExcelVo, AnalysisContext context) {
        // 数据校验：如果数据不存在，才插入；如果数据已存在，则不插入
        // 1.如果id已存在，则不插入
        // 2.同一级pid下，所有的分类不能重名
        if (categoryService.categoryExists(categoryExcelVo)) {
            return;
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryExcelVo, category);
        categoryList.add(category);
        // 集合中的元素达到阈值
        if (categoryList.size() >= limit) {
            categoryService.saveBatch(categoryList);
            categoryList.clear();
        }
    }

    // 整个文件读取完毕后回调一次
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!categoryList.isEmpty()) {
            categoryService.saveBatch(categoryList);
            categoryList.clear();
        }
    }
}
