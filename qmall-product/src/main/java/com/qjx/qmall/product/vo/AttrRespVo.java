package com.qjx.qmall.product.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Ryan
 * 2021-10-17-13:41
 */
@EqualsAndHashCode(callSuper = true)
@Data()
@ApiModel(value = "属性信息+三级分类名字+分组名字")
public class AttrRespVo extends AttrVo{

	@ApiModelProperty(value = "三级分类名字")
	private String catelogName;

	@ApiModelProperty(value = "所属分组名字")
	private String groupName;

	@ApiModelProperty(value = "三级分类全路径")
	private Long[] catelogPath;
}
