package com.qjx.qmall.common.to.mq;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Ryan
 * 2021-11-30-15:50
 */
@Data
public class StockDetailTo {
	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@ApiModelProperty(value = "sku_id")
	private Long skuId;

	@ApiModelProperty(value = "sku_name")
	private String skuName;

	@ApiModelProperty(value = "购买个数")
	private Integer skuNum;

	@ApiModelProperty(value = "工作单id")
	private Long taskId;

	@ApiModelProperty(value = "仓库id")
	private Long wareId;


	@ApiModelProperty(value = "锁定状态")
	private Integer lockStatus;
}
