package com.qjx.qmall.coupon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 秒杀活动场次
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sms_seckill_session")
@ApiModel(value="SeckillSessionEntity对象", description="秒杀活动场次")
public class SeckillSessionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
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

    @TableField(exist = false)
    private List<SeckillSkuRelationEntity> relationSkus;

}
