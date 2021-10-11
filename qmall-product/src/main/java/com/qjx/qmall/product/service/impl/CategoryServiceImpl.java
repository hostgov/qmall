package com.qjx.qmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.product.dao.CategoryDao;
import com.qjx.qmall.product.entity.CategoryEntity;
import com.qjx.qmall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public List<CategoryEntity> listWithTree() {
		//1.查出所有分类
		List<CategoryEntity> list = baseMapper.selectList(null);
		//2.组装成树形结构
		List<CategoryEntity> level1CategoryEntities = list.stream()
				.filter(categoryEntity -> categoryEntity.getParentCid() == 0)
				.peek((level1) -> level1.setChildren(findChildren(level1,list)))
				.sorted(Comparator.comparingInt(CategoryEntity::getSort))
				.collect(Collectors.toList());

		return level1CategoryEntities;
	}



	private List<CategoryEntity> findChildren(CategoryEntity entity, List<CategoryEntity> list) {

		List<CategoryEntity> children = list.stream()
				.filter(entityInList -> entityInList.getParentCid().equals(entity.getCatId()))
				.peek(entityInList -> entityInList.setChildren(findChildren(entityInList,list)))
				.sorted(Comparator.comparingInt(CategoryEntity::getSort))
				.collect(Collectors.toList());
		return children;
	}


	@Override
	public void removeMenuByIds(List<Long> list) {
    	//TODO 检查当前删除的商品类别,是否被其他地方调用

		baseMapper.deleteBatchIds(list);
	}

}

