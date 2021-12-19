package com.qjx.qmall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 退货原因
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@ToString
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oms_order_return_reason")
@ApiModel(value="OrderReturnReasonEntity对象", description="退货原因")
public class OrderReturnReasonEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "退货原因名")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "启用状态")
    private Integer status;

    @ApiModelProperty(value = "create_time")
    private Date createTime;


}
