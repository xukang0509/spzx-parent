package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.ProductSku;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.service.ProductSkuService;
import org.springframework.stereotype.Service;

@Service
public class ProductSkuServiceImpl extends ServiceImpl<ProductSkuMapper, ProductSku> implements ProductSkuService {

}
