package com.qjx.qmall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.member.dao.MemberSocialRelationDao;
import com.qjx.qmall.member.entity.MemberSocialRelationEntity;
import com.qjx.qmall.member.service.MemberSocialRelationService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Ryan
 * 2021-11-17-15:31
 */
@Service("memberSocialRelationService")
public class MemberSocialRelationServiceImpl extends ServiceImpl<MemberSocialRelationDao, MemberSocialRelationEntity> implements MemberSocialRelationService {
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<MemberSocialRelationEntity> page = this.page(
				new Query<MemberSocialRelationEntity>().getPage(params),
				new QueryWrapper<MemberSocialRelationEntity>()
		);

		return new PageUtils(page);
	}
}
