package com.qjx.qmall.product.dao;

import com.qjx.qmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
