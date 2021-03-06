package com.qjx.qmall.ware.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 库存工作单
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wms_ware_order_task_detail")
@ApiModel(value="WareOrderTaskDetailEntity对象", description="库存工作单")
public class WareOrderTaskDetailEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
