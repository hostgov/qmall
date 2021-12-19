package com.qjx.qmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.constant.ProductConstant;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.product.dao.AttrAttrgroupRelationDao;
import com.qjx.qmall.product.dao.AttrDao;
import com.qjx.qmall.product.dao.AttrGroupDao;
import com.qjx.qmall.product.dao.CategoryDao;
import com.qjx.qmall.product.entity.AttrAttrgroupRelationEntity;
import com.qjx.qmall.product.entity.AttrEntity;
import com.qjx.qmall.product.entity.AttrGroupEntity;
import com.qjx.qmall.product.entity.CategoryEntity;
import com.qjx.qmall.product.service.AttrService;
import com.qjx.qmall.product.service.CategoryService;
import com.qjx.qmall.product.vo.AttrGroupRelationVo;
import com.qjx.qmall.product.vo.AttrRespVo;
import com.qjx.qmall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

	@Resource
	AttrAttrgroupRelationDao relationDao;

	@Resource
	AttrGroupDao attrGroupDao;

	@Resource
	CategoryDao categoryDao;

	@Resource
	CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	@Transactional
	public void saveAttrVo(AttrVo vo) {
    	AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(vo,attrEntity);
		this.save(attrEntity);

		//基本属性才有分组信息,所以要保存关联关系
		if (vo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && vo.getAttrGroupId() != null) {
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			relationEntity.setAttrGroupId(vo.getAttrGroupId());
			relationEntity.setAttrId(attrEntity.getAttrId());
			relationDao.insert(relationEntity);
		}

	}

	@Override
	public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
		QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
				.eq("attr_type","base".equalsIgnoreCase(type) ?
						ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
						: ProductConstant.AttrEnum.ATTR_TYPE_SASL);

		if (catelogId != 0) {
			queryWrapper.eq("catelog_id", catelogId);
		}
		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			queryWrapper.and((wrapper) -> wrapper.eq("attr_id",key).or().like("attr_name",key));
		}

		IPage<AttrEntity> page = this.page(
				new Query<AttrEntity>().getPage(params),
				queryWrapper
		);

		PageUtils pageUtils = new PageUtils(page);

		List<AttrEntity> records = page.getRecords();
		List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
			AttrRespVo attrRespVo = new AttrRespVo();
			BeanUtils.copyProperties(attrEntity, attrRespVo);

			if ("base".equalsIgnoreCase(type)) {
				AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
						new QueryWrapper<AttrAttrgroupRelationEntity>()
								.eq("attr_id", attrEntity.getAttrId()));

				if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
					AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
					attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
				}
			}



			CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
			if (categoryEntity != null) {
				attrRespVo.setCatelogName(categoryEntity.getName());
			}
			return attrRespVo;
		}).collect(Collectors.toList());
		pageUtils.setList(respVos);
		return pageUtils;
	}

	@Cacheable(value = "attr", key = "'attrinfo:' + #root.args[0]")
	@Override
	public AttrRespVo getAttrInfo(Long attrId) {

    	AttrRespVo vo = new AttrRespVo();
		AttrEntity attrEntity = this.getById(attrId);
		BeanUtils.copyProperties(attrEntity, vo);


		if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
			AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
			if (relationEntity != null) {
				vo.setAttrGroupId(relationEntity.getAttrGroupId());
				AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrId());
				if (attrGroupEntity != null) {
					vo.setGroupName(attrGroupEntity.getAttrGroupName());
				}
			}
		}



		Long catelogId = attrEntity.getCatelogId();
		Long[] catelogPath = categoryService.findCatelogPath(catelogId);
		vo.setCatelogPath(catelogPath);
		CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
		if (categoryEntity != null) {
			vo.setCatelogName(categoryEntity.getName());
		}

		return vo;
	}

	@Transactional
	@Override
	public void updateAttr(AttrVo attrVo) {
    	AttrEntity attrEntity = new AttrEntity();
    	BeanUtils.copyProperties(attrVo, attrEntity);
		this.updateById(attrEntity);

		if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
			relationEntity.setAttrId(attrVo.getAttrId());

			Integer count = relationDao.selectCount(
					new QueryWrapper<AttrAttrgroupRelationEntity>()
							.eq("attr_id", attrVo.getAttrId()));


			if (count > 0) {
				relationDao.update(
						relationEntity,
						new UpdateWrapper<AttrAttrgroupRelationEntity>()
								.eq("attr_id",attrVo.getAttrId()));
			} else {
				relationDao.insert(relationEntity);
			}
		}



	}

	//根据分组id查找关联的基本属性(规格参数)
	@Override
	public List<AttrEntity> getRelationAttr(Long attrgroupId) {
		List<AttrAttrgroupRelationEntity> list = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
				.eq("attr_group_id", attrgroupId));

		List<Long> attrIds = list.stream()
				.map(AttrAttrgroupRelationEntity::getAttrId)
				.collect(Collectors.toList());

		if(attrIds == null || attrIds.size() == 0) {
			return null;
		}
		List<AttrEntity> attrEntities = this.listByIds(attrIds);
		return attrEntities;
	}

	@Override
	public void deleteRelation(AttrGroupRelationVo[] vos) {
		//relationDao.delete(new QueryWrapper<>().eq("attr_id",).eq("attr_group_id",))
		List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			BeanUtils.copyProperties(item, relationEntity);
			return relationEntity;
		}).collect(Collectors.toList());
		relationDao.deleteBatchRelation(entities);
	}

	// 获取当前分组没有关联的所有属性,这些属性也没有被其他属性关联
	@Override
	public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
		// 1. 当前分组只能关联自己所属的分类里面的所有属性
		AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
		Long catelogId = attrGroupEntity.getCatelogId();
		// 2. 当前分组只能关联别的分组没有引用的属性
		// 2.1 当前分类下的其他分组
		List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
				.eq("catelog_id", catelogId));
		List<Long> collect = attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId)
				.collect(Collectors.toList());


		// 2.2 这些分组关联的属性
		List<AttrAttrgroupRelationEntity> groupIds = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
				.in("attr_group_id", collect));
		List<Long> attrIds = groupIds.stream().map(AttrAttrgroupRelationEntity::getAttrId)
				.collect(Collectors.toList());

		// 2.3 从当前分类的所有属性中移除这些属性
		QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId)
				.eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
		if (attrIds.size() > 0) {
			queryWrapper.notIn("attr_id", attrIds);
		}


		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			queryWrapper.and((w) -> {
				w.eq("attr_id", key).or().like("attr_name", key);
			});
		}
		IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
		PageUtils pageUtils = new PageUtils(page);
		return pageUtils;
	}

	@Override
	public List<Long> selectSearchAttrIds(List<Long> attrIds) {

    	return baseMapper.selectSearchAttrIds(attrIds);
	}

}
