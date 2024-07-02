package com.spzx.product.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Category;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.CategoryService;
import com.spzx.product.vo.CategoryExcelVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商品分类service业务层处理
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> treeSelect(Long parentId) {
        List<Category> categoryList = this.list(Wrappers.lambdaQuery(Category.class).eq(Category::getParentId, parentId));
        if (!CollectionUtils.isEmpty(categoryList)) {
            categoryList.forEach(category -> {
                Long count = categoryMapper.selectCount(Wrappers.lambdaQuery(Category.class)
                        .eq(Category::getParentId, category.getId()));
                if (count > 0) {
                    category.setHasChildren(true);
                } else {
                    category.setHasChildren(false);
                }
            });
        }
        return categoryList;
    }

    @Override
    public void exportCategory(HttpServletResponse response) {
        try {
            // 设置响应结果类型
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("分类数据", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            // 查询数据库中的数据
            List<Category> categoryList = this.list();
            List<CategoryExcelVo> categoryExcelVoList = new ArrayList<>();
            // 将从数据库中查询到的Category对象转换成CategoryExcelVo对象
            categoryList.forEach(category -> {
                CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
                BeanUtils.copyProperties(category, categoryExcelVo);
                categoryExcelVoList.add(categoryExcelVo);
            });
            // 写出数据库到浏览器端
            EasyExcel.write(response.getOutputStream(), CategoryExcelVo.class).sheet("分类数据")
                    .doWrite(categoryExcelVoList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importCategory(MultipartFile file) {
        // 使用EasyExcel导入数据
        try {
            List<CategoryExcelVo> categoryExcelVoList = EasyExcel.read(file.getInputStream()).head(CategoryExcelVo.class).sheet().doReadSync();
            if (!CollectionUtils.isEmpty(categoryExcelVoList)) {
                List<Category> categoryList = new ArrayList<>(categoryExcelVoList.size());
                categoryExcelVoList.forEach(categoryExcelVo -> {
                    Category category = new Category();
                    category.setCreateBy(SecurityUtils.getUsername());
                    category.setCreateTime(new Date());
                    BeanUtils.copyProperties(categoryExcelVo, category);
                    categoryList.add(category);
                });
                this.saveBatch(categoryList);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> getAllCategoryIdList(Long id) {
        List<Long> categoryIds = new ArrayList<>();
        List<Category> parentCategoryList = getParentCategory(id, new ArrayList<>());
        for (int index = parentCategoryList.size() - 1; index >= 0; index--) {
            categoryIds.add(parentCategoryList.get(index).getId());
        }
        return categoryIds;
    }

    private List<Category> getParentCategory(Long id, List<Category> categoryList) {
        if (id <= 0) return categoryList;
        Category category = categoryMapper.selectOne(Wrappers.lambdaQuery(Category.class)
                .eq(Category::getId, id)
                .select(Category::getId, Category::getParentId));
        categoryList.add(category);
        return getParentCategory(category.getParentId(), categoryList);
    }
}
