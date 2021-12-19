package com.qjx.qmall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qjx.qmall.product.entity.AttrGroupEntity;
import com.qjx.qmall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

	List<SpuItemAttrGroupVo> getAttrGroupWithAttrBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
