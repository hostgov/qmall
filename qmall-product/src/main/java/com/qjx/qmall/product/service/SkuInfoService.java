package com.qjx.qmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.product.entity.SkuInfoEntity;
import com.qjx.qmall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveSkuInfo(SkuInfoEntity skuInfoEntity);

	PageUtils queryPageByCondition(Map<String, Object> params);

	List<SkuInfoEntity> getSkuBySpiId(Long spuId);

	SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;
}

