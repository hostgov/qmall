package com.qjx.qmall.product.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.qjx.qmall.product.entity.AttrEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Ryan
 * 2021-10-19-13:33
 */
@Data
public class AttrGroupWithAttrsVo {
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

	@ApiModelProperty(value = "分组下的所有属性信息")
	private List<AttrEntity> attrs;
}
