package com.qjx.qmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 商品属性
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pms_attr")
@ApiModel(value="AttrEntity对象", description="商品属性")
public class AttrEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "属性id")
      @TableId(value = "attr_id", type = IdType.AUTO)
    private Long attrId;

    @ApiModelProperty(value = "属性名")
    private String attrName;

    @ApiModelProperty(value = "是否需要检索[0-不需要，1-需要]")
    private Integer searchType;

    @ApiModelProperty(value = "属性图标")
    private String icon;

    @ApiModelProperty(value = "可选值列表[用逗号分隔]")
    private String valueSelect;

    @ApiModelProperty(value = "属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]")
    private Integer attrType;

    @ApiModelProperty(value = "值类型[0 - 只能单个值, 1 - 允许多个值]")
    private Integer valueType;

    @ApiModelProperty(value = "启用状态[0 - 禁用，1 - 启用]")
    private Long enable;

    @ApiModelProperty(value = "所属分类")
    private Long catelogId;

    @ApiModelProperty(value = "快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整")
    private Integer showDesc;


}
