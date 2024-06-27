package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.SkuStock;
import com.spzx.product.mapper.SkuStockMapper;
import com.spzx.product.service.SkuStockService;
import org.springframework.stereotype.Service;

@Service
public class SkuStockServiceImpl extends ServiceImpl<SkuStockMapper, SkuStock> implements SkuStockService {

}
