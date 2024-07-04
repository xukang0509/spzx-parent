package com.spzx.channel.service.impl;

import com.spzx.channel.service.BrandService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteBrandService;
import com.spzx.product.api.domain.BrandVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Resource
    private RemoteBrandService remoteBrandService;

    @Override
    public List<BrandVo> getBrandAll() {
        R<List<BrandVo>> brandVoListRes = remoteBrandService.getBrandVoList(SecurityConstants.INNER);
        if (brandVoListRes.getCode() == R.FAIL) {
            throw new ServiceException(brandVoListRes.getMsg());
        }
        return brandVoListRes.getData();
    }
}
