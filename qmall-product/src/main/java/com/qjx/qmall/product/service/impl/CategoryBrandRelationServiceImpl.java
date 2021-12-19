package com.qjx.qmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.product.dao.BrandDao;
import com.qjx.qmall.product.dao.CategoryBrandRelationDao;
import com.qjx.qmall.product.dao.CategoryDao;
import com.qjx.qmall.product.entity.BrandEntity;
import com.qjx.qmall.product.entity.CategoryBrandRelationEntity;
import com.qjx.qmall.product.entity.CategoryEntity;
import com.qjx.qmall.product.service.BrandService;
import com.qjx.qmall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

	@Resource
	BrandDao brandDao;

	@Resource
	CategoryDao categoryDao;

	@Resource
	CategoryBrandRelationDao relationDao;

	@Resource
	BrandService brandService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
		Long brandId = categoryBrandRelation.getBrandId();
		Long catelogId = categoryBrandRelation.getCatelogId();

		BrandEntity brandEntity = brandDao.selectById(brandId);
		CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
		categoryBrandRelation.setBrandName(brandEntity.getName());
		categoryBrandRelation.setCatelogName(categoryEntity.getName());

		this.save(categoryBrandRelation);
	}

	@Override
	public void updateBrand(Long brandId, String name) {
		CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
		relationEntity.setBrandId(brandId);
		relationEntity.setBrandName(name);
		this.update(relationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
	}

	@Override
	public void updateCategory(Long catId, String name) {
		this.baseMapper.updateCategory(catId, name);
	}

	@Override
	public List<BrandEntity> getBrandsByCatId(Long catId) {
		List<CategoryBrandRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>()
				.eq("catelog_id", catId));
		List<BrandEntity> collect = relationEntities.stream().map(item -> {
			Long brandId = item.getBrandId();
			BrandEntity brandEntity = brandService.getById(brandId);
			return brandEntity;
		}).collect(Collectors.toList());
		return collect;
	}

}
