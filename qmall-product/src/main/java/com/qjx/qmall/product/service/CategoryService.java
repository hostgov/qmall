package com.qjx.qmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.product.entity.CategoryEntity;
import com.qjx.qmall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<CategoryEntity> listWithTree();

	void removeMenuByIds(List<Long> asList);

	Long[] findCatelogPath(Long catelogId);

	void updateCasecadeById(CategoryEntity category);

	List<CategoryEntity> getLevel1Categories();

	Map<String, List<Catelog2Vo>> getCatalogJson();
}

