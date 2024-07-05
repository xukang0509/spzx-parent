package com.spzx.product.api;

import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.domain.BrandVo;
import com.spzx.product.api.factory.RemoteBrandFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 远程品牌服务
 */
@FeignClient(contextId = "remoteBrandService", value = ServiceNameConstants.PRODUCT_SERVICE,
        fallbackFactory = RemoteBrandFallbackFactory.class)
public interface RemoteBrandService {
    @GetMapping("/brand/getAllBrandVo")
    R<List<BrandVo>> getBrandVoList();
}
