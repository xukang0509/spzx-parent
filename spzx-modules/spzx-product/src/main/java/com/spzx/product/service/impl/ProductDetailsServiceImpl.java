package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.ProductDetails;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.service.ProductDetailsService;
import org.springframework.stereotype.Service;

@Service
public class ProductDetailsServiceImpl extends ServiceImpl<ProductDetailsMapper, ProductDetails>
        implements ProductDetailsService {

}
