package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.*;

import java.util.List;
import java.util.Map;

/**
 * 商品Service接口
 *
 * @author spzx
 */
public interface ProductService extends IService<Product> {
    /**
     * 查询商品列表
     *
     * @param product 商品
     * @return 商品集合
     */
    List<Product> selectProductList(Product product);

    /**
     * 新增商品
     *
     * @param product 商品
     * @return 结果
     */
    int insertProduct(Product product);

    /**
     * 根据主键ID查询商品
     *
     * @param id 商品主键ID
     * @return 商品
     */
    Product selectProductById(Long id);

    /**
     * 更新商品
     *
     * @param product 商品
     * @return 结果
     */
    int updateProduct(Product product);

    /**
     * 批量删除商品
     *
     * @param ids 商品主键ID集合
     * @return 结果
     */
    int deleteProductsByIds(Long[] ids);

    /**
     * 商品审核
     *
     * @param id          商品主键ID
     * @param auditStatus 审核状态：0-初始值，1-通过，-1-未通过
     */
    void updateAuditStatus(Long id, Integer auditStatus);

    /**
     * 更新上下架状态
     *
     * @param id     商品主键ID
     * @param status 线上状态：0-初始值，1-上架，-1-自主下架
     */
    void updateStatus(Long id, Integer status);

    List<ProductSku> getTopSale();

    List<ProductSku> selectProductSkuList(SkuQuery skuQuery);

    ProductSku getProductSkuBySkuId(Long skuId);

    Product getProductById(Long productId);

    ProductDetails getProductDetailsByProductId(Long productId);

    SkuStockVo getSkuStockVoBySkuId(Long skuId);

    Map<String, Long> getSkuSpecValueMapByProductId(Long productId);

    List<SkuPrice> getSkuPriceListBySkuIds(List<Long> skuIds);

    SkuPrice getSkuPriceBySkuId(Long skuId);

    String checkAndLock(String orderNo, List<SkuLockVo> skuLockVoList);

    void unlock(String orderNo);

    void minus(String orderNo);
}
