package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.SkuStock;
import org.apache.ibatis.annotations.Param;

public interface SkuStockMapper extends BaseMapper<SkuStock> {

    SkuStock check(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    Integer lock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    Integer unlock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    Integer minus(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}