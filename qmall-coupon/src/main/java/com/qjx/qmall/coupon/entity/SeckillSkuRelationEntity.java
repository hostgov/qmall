package com.qjx.qmall.coupon.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 秒杀活动商品关联
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sms_seckill_sku_relation")
@ApiModel(value="SeckillSkuRelationEntity对象", description="秒杀活动商品关联")
public class SeckillSkuRelationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "活动id")
    private Long promotionId;

    @ApiModelProperty(value = "活动场次id")
    private Long promotionSessionId;

    @ApiModelProperty(value = "商品id")
    private Long skuId;

    @ApiModelProperty(value = "秒杀价格")
    private BigDecimal seckillPrice;

    @ApiModelProperty(value = "秒杀总量")
    private BigDecimal seckillCount;

    @ApiModelProperty(value = "每人限购数量")
    private BigDecimal seckillLimit;

    @ApiModelProperty(value = "排序")
    private Integer seckillSort;


}
