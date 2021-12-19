package com.qjx.qmall.product.vo;

import com.qjx.qmall.product.entity.SkuImagesEntity;
import com.qjx.qmall.product.entity.SkuInfoEntity;
import com.qjx.qmall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * Ryan
 * 2021-11-13-14:29
 */
@Data
public class SkuItemVo {
	//1. sku基本信息获取  `pms_sku_info`
	SkuInfoEntity info;

	boolean hasStock = true;

	//2. sku图片信息 `pms_sku_images`
	List<SkuImagesEntity> images;
	//3. 获取spu销售属性组合
	List<SkuItemSaleAttrVo> saleAttr;

	//4. 获取spu的介绍`pms_spu_info_desc`
	SpuInfoDescEntity desp;

	//5. 获取spu的规格参数信息
	List<SpuItemAttrGroupVo> groupAttrs;

	//6.当前商品的秒杀优惠信息
	SeckillInfoVo seckillInfo;

}
