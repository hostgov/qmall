package com.qjx.qmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.product.dao.CategoryDao;
import com.qjx.qmall.product.entity.CategoryEntity;
import com.qjx.qmall.product.service.CategoryBrandRelationService;
import com.qjx.qmall.product.service.CategoryService;
import com.qjx.qmall.product.vo.Catelog2Vo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


	@Resource
	CategoryBrandRelationService categoryBrandRelationService;

	@Resource
	StringRedisTemplate stringRedisTemplate;

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

	@Override
	public Long[] findCatelogPath(Long catelogId) {
		List<Long> paths = new ArrayList<>();

		List<Long> parentPath = findParentPath(catelogId, paths);
		Collections.reverse(parentPath);


		return parentPath.toArray(new Long[parentPath.size()]);
	}



//	@Caching(evict = {
//			@CacheEvict(value = "category", key = "'getLevel1Categories'"),
//			@CacheEvict(value = "category", key = "'getCatalogJson'")
//	})
	@CacheEvict(value = "category", allEntries = true)
	@Override
	@Transactional
	public void updateCasecadeById(CategoryEntity category) {
		this.updateById(category);
		categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

	}

	@Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
	@Override
	public List<CategoryEntity> getLevel1Categories() {

    	return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
	}


	@Cacheable(value = "category", key = "#root.methodName")
	@Override
	public Map<String, List<Catelog2Vo>> getCatalogJson() {

		System.out.println("缓存不命中. 查询数据库");
		//优化 1 将数据库的多次查询变为一次
		List<CategoryEntity> selectList = baseMapper.selectList(null);


		//1. 查出所有1级分类
		List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);

		//2. 封装数据
		Map<String, List<Catelog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(
				k -> k.getCatId().toString(),
				v -> {
					List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
					List<Catelog2Vo> catelog2Vos = null;
					if (categoryEntities != null) {
						catelog2Vos = categoryEntities.stream().map(l2 -> {
							Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
							// 找当前二级分类的三级分类封装成vo
							List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
							if (level3Catelog != null) {
								List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName())).collect(Collectors.toList());
								catelog2Vo.setCatalog3List(collect);
							}
							return catelog2Vo;
						}).collect(Collectors.toList());
					}
					return catelog2Vos;
				})
		);

		return parent_cid;
	}

	public Map<String, List<Catelog2Vo>> getCatalogJson2() {
    	//加入缓存逻辑
		String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
		if (StringUtils.isEmpty(catalogJSON)) {
			//如果缓存中没有,查询数据库

			return getCatalogJsonFromDb();
		}

		//转为我们指定的对象



		Map<String, List<Catelog2Vo>> res = JSON.parseObject(catalogJSON,new TypeReference<Map<String, List<Catelog2Vo>>>(){});
		return res;
	}



	//从数据库查询并封装分类数据
	public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {


    	synchronized (this) {

		    String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
		    if (!StringUtils.isEmpty(catalogJSON)) {
		    	return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
		    }


		    System.out.println("缓存不命中. 查询数据库");
		    //优化 1 将数据库的多次查询变为一次
		    List<CategoryEntity> selectList = baseMapper.selectList(null);


		    //1. 查出所有1级分类
		    List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);

		    //2. 封装数据
		    Map<String, List<Catelog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(
				    k -> k.getCatId().toString(),
				    v -> {
					    List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
					    List<Catelog2Vo> catelog2Vos = null;
					    if (categoryEntities != null) {
						    catelog2Vos = categoryEntities.stream().map(l2 -> {
							    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
							    // 找当前二级分类的三级分类封装成vo
							    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
							    if (level3Catelog != null) {
								    List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName())).collect(Collectors.toList());
								    catelog2Vo.setCatalog3List(collect);
							    }
							    return catelog2Vo;
						    }).collect(Collectors.toList());
					    }
					    return catelog2Vos;
				    })
		    );
		    //将查到的数据放入缓存, 将查出的对象转换成json放入缓存
		    String s = JSON.toJSONString(parent_cid);
		    stringRedisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
		    return parent_cid;
	    }

	}

	private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
		return selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
		//return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
	}

	private List<Long> findParentPath(Long catelogId, List<Long> paths) {
    	paths.add(catelogId);
		CategoryEntity byId = this.getById(catelogId);
		if (byId.getParentCid() != 0) {
			findParentPath(byId.getParentCid(), paths);
		}
		return paths;
	}

}

