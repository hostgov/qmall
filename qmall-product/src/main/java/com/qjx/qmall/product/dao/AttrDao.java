package com.qjx.qmall.product.dao;

import com.qjx.qmall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

	List<Long> selectSearchAttrIds(@Param("attrIds") List<Long> attrIds);
}
