package com.qjx.qmall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qjx.qmall.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

	void deleteBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> entities);
}
