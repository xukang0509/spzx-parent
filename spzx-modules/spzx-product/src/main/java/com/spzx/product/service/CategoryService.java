package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.Category;
import com.spzx.product.vo.CategoryExcelVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 商品分类导出
     *
     * @param response 响应报文
     */
    void exportCategory(HttpServletResponse response);

    /**
     * 商品分类导入
     *
     * @param file 文件
     */
    void importCategory(MultipartFile file);

    /**
     * 下载模版
     *
     * @param response 响应报文
     */
    void downloadTemplate(HttpServletResponse response);

    boolean categoryExists(CategoryExcelVo categoryExcelVo);
}
