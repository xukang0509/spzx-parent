package com.spzx.channel.service.impl;

import com.spzx.channel.service.IndexService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.CategoryVo;
import com.spzx.product.api.domain.ProductSku;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexServiceImpl implements IndexService {
    @Resource
    private RemoteCategoryService remoteCategoryService;
    @Resource
    private RemoteProductService remoteProductService;

    @Override
    public Map<String, Object> getIndexData() {
        R<List<CategoryVo>> oneCategoryRes = remoteCategoryService.getOneCategory(SecurityConstants.INNER);
        if (R.FAIL == oneCategoryRes.getCode()) {
            throw new ServiceException(oneCategoryRes.getMsg());
        }
        R<List<ProductSku>> topSaleRes = remoteProductService.getTopSale(SecurityConstants.INNER);
        if (R.FAIL == topSaleRes.getCode()) {
            throw new ServiceException(topSaleRes.getMsg());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("categoryList", oneCategoryRes.getData());
        map.put("productSkuList", topSaleRes.getData());
        return map;
    }
}
