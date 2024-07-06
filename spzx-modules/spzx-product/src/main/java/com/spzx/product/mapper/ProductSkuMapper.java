package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductSkuMapper extends BaseMapper<ProductSku> {
    List<ProductSku> getTopSale();

    List<ProductSku> selectProductSkuList(@Param("query") SkuQuery skuQuery);

    List<Long> getProductSkuIdsOnSale();
}