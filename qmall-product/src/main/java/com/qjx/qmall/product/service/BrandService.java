package com.qjx.qmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void updateDetailById(BrandEntity brand);

	List<BrandEntity> getBrandsByIds(List<Long> brandIds);
}

