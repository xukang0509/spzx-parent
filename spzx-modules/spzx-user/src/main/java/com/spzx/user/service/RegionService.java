package com.spzx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.domain.Region;

import java.util.List;

/**
 * 地区信息Service接口
 *
 * @author xukang
 * @date 2024-07-03
 */
public interface RegionService extends IService<Region> {
    /**
     * 根据上级地区code查找地区信息
     *
     * @param parentCode 上级地区code
     * @return 地区信息集合
     */
    List<Region> treeSelect(String parentCode);

    /**
     * 根据code获取地区名称
     *
     * @param code
     * @return
     */
    String getNameByCode(String code);
}
