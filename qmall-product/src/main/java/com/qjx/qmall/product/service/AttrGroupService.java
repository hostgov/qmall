package com.qjx.qmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.product.entity.AttrGroupEntity;
import com.qjx.qmall.product.vo.AttrGroupWithAttrsVo;
import com.qjx.qmall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

	PageUtils queryPage(Map<String, Object> params, Long catelogId);


	List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

	List<SpuItemAttrGroupVo> getAttrGroupWithAttrBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}

