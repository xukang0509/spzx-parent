package com.spzx.channel.service;

import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.domain.SkuQuery;

public interface ListService {
    TableDataInfo skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery);
}
