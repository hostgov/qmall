package com.qjx.qmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.product.dao.AttrGroupDao;
import com.qjx.qmall.product.entity.AttrEntity;
import com.qjx.qmall.product.entity.AttrGroupEntity;
import com.qjx.qmall.product.service.AttrGroupService;
import com.qjx.qmall.product.service.AttrService;
import com.qjx.qmall.product.vo.AttrGroupWithAttrsVo;
import com.qjx.qmall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

	@Resource
	AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
		String key = (String) params.get("key");
		//select * from pms_attr_group where catelog_id=? and (attr_group_id = key or attr_group_name like %key%)
		QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper.and((obj) -> {
				obj.eq("attr_group_id", key).or().like("attr_group_name", key);
			});
		}
		if (catelogId == 0) {
			IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
					wrapper);
			return new PageUtils(page);
		} else {
			wrapper.eq("catelog_id", catelogId);
			IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
					wrapper);
			return new PageUtils(page);

		}
	}

	//根据三级分类id查出所有的分组以及分组下的属性信息
	@Override
	public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
		//1.查询分组信息
		List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
		//2.查询分组下的属性信息
		List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
			AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
			BeanUtils.copyProperties(group, vo);
			List<AttrEntity> attrs = attrService.getRelationAttr(vo.getAttrGroupId());
			vo.setAttrs(attrs);
			return vo;
		}).collect(Collectors.toList());
		return collect;
	}

	@Override
	public List<SpuItemAttrGroupVo> getAttrGroupWithAttrBySpuId(Long spuId, Long catalogId) {
		//1. 查出当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
		AttrGroupDao baseMapper = this.getBaseMapper();
		List<SpuItemAttrGroupVo> vos =  baseMapper.getAttrGroupWithAttrBySpuId(spuId, catalogId);

		return vos;
	}

}
