package com.qjx.qmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.product.entity.AttrEntity;
import com.qjx.qmall.product.vo.AttrGroupRelationVo;
import com.qjx.qmall.product.vo.AttrRespVo;
import com.qjx.qmall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveAttrVo(AttrVo attr);

	PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

	AttrRespVo getAttrInfo(Long attrId);

	void updateAttr(AttrVo attrVo);

	List<AttrEntity> getRelationAttr(Long attrgroupId);

	void deleteRelation(AttrGroupRelationVo[] vos);

	PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

	//在指定的所有规格属性集合中挑出可以用于检索的规格属性id
	List<Long> selectSearchAttrIds(List<Long> attrIds);
}

