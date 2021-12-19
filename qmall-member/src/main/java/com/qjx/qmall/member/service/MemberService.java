package com.qjx.qmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.member.entity.MemberEntity;
import com.qjx.qmall.member.exception.PhoneExistException;
import com.qjx.qmall.member.exception.UserNameExistException;
import com.qjx.qmall.member.vo.MemberEntityWithSocialVo;
import com.qjx.qmall.member.vo.MemberLoginVo;
import com.qjx.qmall.member.vo.MemberRegistVo;
import com.qjx.qmall.member.vo.WeiboTokenResponse;

import java.util.Map;

/**
 * 会员
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:09:10
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void regist(MemberRegistVo vo);

	void checkPhoneUnique(String phone) throws PhoneExistException;
	void checkUsernameUnique(String username) throws UserNameExistException;

	MemberEntityWithSocialVo login(MemberLoginVo vo);

	MemberEntityWithSocialVo login(WeiboTokenResponse weiboTokenResponse) throws Exception;
}

