package com.qjx.qmall.product.dao;

import com.qjx.qmall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

	void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
