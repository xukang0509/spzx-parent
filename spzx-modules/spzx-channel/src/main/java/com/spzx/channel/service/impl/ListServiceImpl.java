package com.spzx.channel.service.impl;

import com.spzx.channel.service.ListService;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.SkuQuery;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ListServiceImpl implements ListService {
    @Resource
    private RemoteProductService remoteProductService;

    @Override
    public TableDataInfo skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery) {
        R<TableDataInfo> tableDataInfoRes = remoteProductService.skuList(pageNum, pageSize, skuQuery);
        if (tableDataInfoRes.getCode() == R.FAIL) {
            throw new ServiceException(tableDataInfoRes.getMsg());
        }
        return tableDataInfoRes.getData();
    }
}
