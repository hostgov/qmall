package com.qjx.qmall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 库存工作单
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wms_ware_order_task")
@ApiModel(value="WareOrderTaskEntity对象", description="库存工作单")
public class WareOrderTaskEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "order_id")
    private Long orderId;

    @ApiModelProperty(value = "order_sn")
    private String orderSn;

    @ApiModelProperty(value = "收货人")
    private String consignee;

    @ApiModelProperty(value = "收货人电话")
    private String consigneeTel;

    @ApiModelProperty(value = "配送地址")
    private String deliveryAddress;

    @ApiModelProperty(value = "订单备注")
    private String orderComment;

    @ApiModelProperty(value = "付款方式【 1:在线付款 2:货到付款】")
    private Integer paymentWay;

    @ApiModelProperty(value = "任务状态")
    private Integer taskStatus;

    @ApiModelProperty(value = "订单描述")
    private String orderBody;

    @ApiModelProperty(value = "物流单号")
    private String trackingNo;

    @ApiModelProperty(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    @ApiModelProperty(value = "工作单备注")
    private String taskComment;


}
