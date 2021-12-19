package com.qjx.qmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.product.entity.SpuInfoEntity;
import com.qjx.qmall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveSpuInfo(SpuSaveVo vo);

	void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

	PageUtils queryPageByCondition(Map<String, Object> params);

	void up(Long spuId);

	SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

