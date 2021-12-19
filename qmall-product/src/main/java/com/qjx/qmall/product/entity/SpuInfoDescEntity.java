package com.qjx.qmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * spu信息介绍
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pms_spu_info_desc")
@ApiModel(value="SpuInfoDescEntity对象", description="spu信息介绍")
public class SpuInfoDescEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
      @TableId(value = "spu_id", type = IdType.INPUT)
    private Long spuId;

    @ApiModelProperty(value = "商品介绍")
    private String decript;


}
