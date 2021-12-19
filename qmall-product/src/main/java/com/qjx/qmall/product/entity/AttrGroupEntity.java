package com.qjx.qmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 属性分组
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pms_attr_group")
@ApiModel(value="AttrGroupEntity对象", description="属性分组")
public class AttrGroupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分组id")
      @TableId(value = "attr_group_id", type = IdType.AUTO)
    private Long attrGroupId;

    @ApiModelProperty(value = "组名")
    private String attrGroupName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "描述")
    private String descript;

    @ApiModelProperty(value = "组图标")
    private String icon;

    @ApiModelProperty(value = "所属分类id")
    private Long catelogId;

    @ApiModelProperty(value = "三级分类的三级路径")
    @TableField(exist = false)
    private Long[] catelogPath;

}
