package com.qjx.qmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.product.dao.SkuSaleAttrValueDao;
import com.qjx.qmall.product.entity.SkuSaleAttrValueEntity;
import com.qjx.qmall.product.service.SkuSaleAttrValueService;
import com.qjx.qmall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {

		List<SkuItemSaleAttrVo> saleAttrVos = baseMapper.getSaleAttrsBySpuId(spuId);
		return saleAttrVos;

	}

	@Override
	public List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {

    	return baseMapper.getSkuSaleAttrValuesAsStringList(skuId);
	}

}
