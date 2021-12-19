package com.qjx.qmall.ware.dao;

import com.qjx.qmall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:11:12
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

	void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

	Long getSkuStock(@Param("skuId") Long skuId);

	List<Long> listWareIdHasStock(@Param("skuId") Long skuId);

	Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("count") Integer count);

	void unlockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
