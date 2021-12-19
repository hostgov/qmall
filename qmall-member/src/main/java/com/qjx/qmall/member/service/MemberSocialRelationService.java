package com.qjx.qmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.member.entity.MemberSocialRelationEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Ryan
 * 2021-11-17-15:29
 */
@Service
public interface MemberSocialRelationService extends IService<MemberSocialRelationEntity> {
	PageUtils queryPage(Map<String, Object> params);
}
