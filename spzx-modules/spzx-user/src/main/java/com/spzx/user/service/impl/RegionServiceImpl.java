package com.spzx.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.user.domain.Region;
import com.spzx.user.mapper.RegionMapper;
import com.spzx.user.service.RegionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地区信息Service业务层处理
 *
 * @author xukang
 * @date 2024-07-03
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {
    @Resource
    private RegionMapper regionMapper;

    @Override
    public List<Region> treeSelect(String parentCode) {
        return regionMapper.selectList(Wrappers.lambdaQuery(Region.class)
                .eq(Region::getParentCode, parentCode));
    }
}
