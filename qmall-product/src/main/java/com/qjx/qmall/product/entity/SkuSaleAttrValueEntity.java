package com.qjx.qmall.product.entity;

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
 * sku销售属性&值
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pms_sku_sale_attr_value")
@ApiModel(value="SkuSaleAttrValueEntity对象", description="sku销售属性&值")
public class SkuSaleAttrValueEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "sku_id")
    private Long skuId;

    @ApiModelProperty(value = "attr_id")
    private Long attrId;

    @ApiModelProperty(value = "销售属性名")
    private String attrName;

    @ApiModelProperty(value = "销售属性值")
    private String attrValue;

    @ApiModelProperty(value = "顺序")
    private Integer attrSort;


}
