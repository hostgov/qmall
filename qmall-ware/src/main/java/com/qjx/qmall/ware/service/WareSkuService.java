package com.qjx.qmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.to.mq.OrderTo;
import com.qjx.qmall.common.to.mq.StockLockedTo;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.ware.entity.WareSkuEntity;
import com.qjx.qmall.ware.vo.SkuHasStockVo;
import com.qjx.qmall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:11:12
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void addStore(Long skuId, Long wareId, Integer skuNum);

	List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

	Boolean orderLockStock(WareSkuLockVo vo);


	void unlockStock(StockLockedTo to);

	void unlockStock(OrderTo orderTo);
}

