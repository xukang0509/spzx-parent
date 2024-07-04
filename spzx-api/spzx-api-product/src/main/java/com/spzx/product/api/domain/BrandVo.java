package com.spzx.product.api.domain;

import lombok.Data;

@Data
public class BrandVo {
    /**
     * 品牌ID
     */
    private Long id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 品牌logo
     */
    private String logo;
}
