package com.qjx.qmall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.to.MemberPrice;
import com.qjx.qmall.common.to.SkuReductionTo;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.coupon.dao.SkuFullReductionDao;
import com.qjx.qmall.coupon.entity.MemberPriceEntity;
import com.qjx.qmall.coupon.entity.SkuFullReductionEntity;
import com.qjx.qmall.coupon.entity.SkuLadderEntity;
import com.qjx.qmall.coupon.service.MemberPriceService;
import com.qjx.qmall.coupon.service.SkuFullReductionService;
import com.qjx.qmall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

	@Resource
	SkuLadderService skuLadderService;

	@Resource
	MemberPriceService memberPriceService;




    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void saveSkuReduction(SkuReductionTo skuReductionTo) {
		//6.4.1 `sms_sku_ladder`
		SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
		skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
		skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
		skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
		skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
		if (skuReductionTo.getFullCount() > 0) {
			skuLadderService.save(skuLadderEntity);
		}

		//6.4.2 `sms_sku_full_reduction`
		SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
		BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
		if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
			this.save(skuFullReductionEntity);
		}


		//6.4.3 `sms_member_price`
		List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
		List<MemberPriceEntity> collect = memberPrices.stream().map(item -> {
			MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
			memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
			memberPriceEntity.setMemberLevelId(item.getId());
			memberPriceEntity.setMemberLevelName(item.getName());
			memberPriceEntity.setMemberPrice(item.getPrice());
			memberPriceEntity.setAddOther(1);
			return memberPriceEntity;
		}).filter(item -> item.getMemberPrice().compareTo(new BigDecimal("0")) == 1)
				.collect(Collectors.toList());
		memberPriceService.saveBatch(collect);
	}

}
