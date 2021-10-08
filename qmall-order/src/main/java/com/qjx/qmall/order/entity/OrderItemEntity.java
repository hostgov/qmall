package com.qjx.qmall.order.entity;

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
 * 订单项信息
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oms_order_item")
@ApiModel(value="OrderItemEntity对象", description="订单项信息")
public class OrderItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "order_id")
    private Long orderId;

    @ApiModelProperty(value = "order_sn")
    private String orderSn;

    @ApiModelProperty(value = "spu_id")
    private Long spuId;

    @ApiModelProperty(value = "spu_name")
    private String spuName;

    @ApiModelProperty(value = "spu_pic")
    private String spuPic;

    @ApiModelProperty(value = "品牌")
    private String spuBrand;

    @ApiModelProperty(value = "商品分类id")
    private Long categoryId;

    @ApiModelProperty(value = "商品sku编号")
    private Long skuId;

    @ApiModelProperty(value = "商品sku名字")
    private String skuName;

    @ApiModelProperty(value = "商品sku图片")
    private String skuPic;

    @ApiModelProperty(value = "商品sku价格")
    private BigDecimal skuPrice;

    @ApiModelProperty(value = "商品购买的数量")
    private Integer skuQuantity;

    @ApiModelProperty(value = "商品销售属性组合（JSON）")
    private String skuAttrsVals;

    @ApiModelProperty(value = "商品促销分解金额")
    private BigDecimal promotionAmount;

    @ApiModelProperty(value = "优惠券优惠分解金额")
    private BigDecimal couponAmount;

    @ApiModelProperty(value = "积分优惠分解金额")
    private BigDecimal integrationAmount;

    @ApiModelProperty(value = "该商品经过优惠后的分解金额")
    private BigDecimal realAmount;

    @ApiModelProperty(value = "赠送积分")
    private Integer giftIntegration;

    @ApiModelProperty(value = "赠送成长值")
    private Integer giftGrowth;


}
