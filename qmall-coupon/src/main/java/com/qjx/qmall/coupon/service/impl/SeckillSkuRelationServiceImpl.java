package com.qjx.qmall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.coupon.dao.SeckillSkuRelationDao;
import com.qjx.qmall.coupon.entity.SeckillSkuRelationEntity;
import com.qjx.qmall.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> queryWrapper = new QueryWrapper<SeckillSkuRelationEntity>();
        String promotionSessionId = (String) params.get("promotionSessionId");
        if (!StringUtils.isEmpty(promotionSessionId)) {
            queryWrapper.eq("promotion_session_id", promotionSessionId);
        }

        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}
