package com.qjx.qmall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.coupon.dao.SeckillSessionDao;
import com.qjx.qmall.coupon.entity.SeckillSessionEntity;
import com.qjx.qmall.coupon.entity.SeckillSkuRelationEntity;
import com.qjx.qmall.coupon.service.SeckillSessionService;
import com.qjx.qmall.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

	@Resource
	SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public List<SeckillSessionEntity> getLatest3DaysSession() {

		List<SeckillSessionEntity> list = list(new QueryWrapper<SeckillSessionEntity>()
				.between("start_time", startTime(), endTime()));
		if (list != null && list.size() > 0) {
			List<SeckillSessionEntity> collect = list.stream().peek(entity -> {
				List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>()
						.eq("promotion_session_id", entity.getId()));
				entity.setRelationSkus(relationEntities);
			}).collect(Collectors.toList());
			return collect;
		}


		return null;

	}

	private String startTime() {
		LocalDate nowDate = LocalDate.now();
		LocalTime min = LocalTime.MIN;
		LocalDateTime start = LocalDateTime.of(nowDate, min);
		return start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

	}
	private String endTime() {
		LocalDate nowDate = LocalDate.now();
		LocalDate endDate = nowDate.plusDays(2);
		LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);
		return end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

}
