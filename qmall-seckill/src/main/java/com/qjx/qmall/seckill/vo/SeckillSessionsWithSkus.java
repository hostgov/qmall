package com.qjx.qmall.seckill.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Ryan
 * 2021-12-05-21:15
 */
@Data
public class SeckillSessionsWithSkus {
	private Long id;

	@ApiModelProperty(value = "场次名称")
	private String name;

	@ApiModelProperty(value = "每日开始时间")
	private Date startTime;

	@ApiModelProperty(value = "每日结束时间")
	private Date endTime;

	@ApiModelProperty(value = "启用状态")
	private Integer status;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	private List<SeckillSkuVo> relationSkus;
}
