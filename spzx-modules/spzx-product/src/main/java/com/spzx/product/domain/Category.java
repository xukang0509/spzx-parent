package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品分类对象 category
 *
 * @author spzx
 */
@Data
@Schema(description = "商品分类")
public class Category extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "图标地址")
    private String imageUrl;

    @Schema(description = "父分类id")
    private Long parentId;

    @Schema(description = "是否显示[0-不显示，1显示]")
    private Integer status;

    @Schema(description = "排序")
    private Long orderNum;

    /* 是否有子节点 */
    @TableField(exist = false)
    private Boolean hasChildren;

    /* 子节点列表 */
    @TableField(exist = false)
    private List<Category> children;
}
