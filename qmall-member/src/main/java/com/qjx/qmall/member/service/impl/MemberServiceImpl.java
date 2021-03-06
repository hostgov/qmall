package com.qjx.qmall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.HttpUtils;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.member.dao.MemberDao;
import com.qjx.qmall.member.dao.MemberLevelDao;
import com.qjx.qmall.member.dao.MemberSocialRelationDao;
import com.qjx.qmall.member.entity.MemberEntity;
import com.qjx.qmall.member.entity.MemberLevelEntity;
import com.qjx.qmall.member.entity.MemberSocialRelationEntity;
import com.qjx.qmall.member.exception.PhoneExistException;
import com.qjx.qmall.member.exception.UserNameExistException;
import com.qjx.qmall.member.service.MemberService;
import com.qjx.qmall.member.vo.MemberEntityWithSocialVo;
import com.qjx.qmall.member.vo.MemberLoginVo;
import com.qjx.qmall.member.vo.MemberRegistVo;
import com.qjx.qmall.member.vo.WeiboTokenResponse;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

	@Resource
	MemberLevelDao memberLevelDao;

	@Resource
	MemberSocialRelationDao memberSocialRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void regist(MemberRegistVo vo) {
		MemberEntity entity = new MemberEntity();
		//??????????????????
		MemberLevelEntity memberDefaultLevel = memberLevelDao.getDefaultLevel();
		entity.setLevelId(memberDefaultLevel.getId());
		//???????????????????????????????????????,?????????controller???????????????,??????????????????
		checkUsernameUnique(vo.getUsername());
		checkPhoneUnique(vo.getPhone());

		entity.setMobile(vo.getPhone());
		entity.setUsername(vo.getUsername());

		entity.setNickname(vo.getUsername());

		//???????????????????????????
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encode = passwordEncoder.encode(vo.getPassword());
		entity.setPassword(encode);

		//??????????????????


		//??????
		baseMapper.insert(entity);
	}

	@Override
	public void checkPhoneUnique(String phone) throws PhoneExistException {
		Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
		if (count > 0) {
			throw new PhoneExistException();
		}



	}

	@Override
	public void checkUsernameUnique(String username) throws UserNameExistException {
		Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
		if (count > 0) {
			throw new UserNameExistException();
		}
	}

	@Override
	public MemberEntityWithSocialVo login(MemberLoginVo vo) {

		String loginacct = vo.getLoginacct();
		String password = vo.getPassword();
		//??????????????????
		MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
				.eq("username", loginacct)
				.or()
				.eq("mobile", loginacct));
		if (entity == null) {
			return null;
		} else {
			String passwordDb = entity.getPassword();
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			boolean matches = passwordEncoder.matches(password, passwordDb);
			if (matches) {
				MemberEntityWithSocialVo memberEntityWithSocialVo = new MemberEntityWithSocialVo();
				BeanUtils.copyProperties(entity, memberEntityWithSocialVo);
				return memberEntityWithSocialVo;
			} else {
				return null;
			}
		}

	}

	@Transactional
	@Override
	public MemberEntityWithSocialVo login(WeiboTokenResponse weiboTokenResponse) throws Exception {
    	//???????????????????????????
		String uid = weiboTokenResponse.getUid();

		String tokenFrom = weiboTokenResponse.getTokenFrom();

		String accessToken = weiboTokenResponse.getAccessToken();

		long expiresIn = weiboTokenResponse.getExpiresIn();

		MemberEntityWithSocialVo vo = new MemberEntityWithSocialVo();
		//?????????????????????????????????????????????
		MemberSocialRelationEntity memberSocialRelationEntity = memberSocialRelationDao.selectOne(new QueryWrapper<MemberSocialRelationEntity>().eq("social_uid", uid).eq("token_from", tokenFrom));
		if (memberSocialRelationEntity != null) {
			//????????????????????????
			MemberSocialRelationEntity update = new MemberSocialRelationEntity();
			update.setId(memberSocialRelationEntity.getId());
			update.setAccessToken(accessToken);
			update.setExpiresIn(expiresIn);
			memberSocialRelationDao.updateById(update);

			MemberEntity memberEntity = baseMapper.selectById(memberSocialRelationEntity.getMemberId());



			BeanUtils.copyProperties(memberEntity, vo);

			vo.setAccessToken(accessToken);
			vo.setExpiresIn(expiresIn);
			vo.setSocialUid(uid);
			vo.setTokenFrom(tokenFrom);
			return vo;
		} else {
			//2. ?????????????????????????????????????????????,??????????????????
			MemberEntity memberEntity = new MemberEntity();
			//?????????????????????????????????????????????(??????,?????????)

			try {
				Map<String, String> query = new HashMap<>();
				query.put("access_token", accessToken);
				query.put("uid",uid);
				HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
				if (response.getStatusLine().getStatusCode() == 200) {
					String json = EntityUtils.toString(response.getEntity());
					JSONObject jsonObject = JSON.parseObject(json);
					String name = jsonObject.getString("name"); //??????
					String gender = jsonObject.getString("gender");
					String header = jsonObject.getString("profile_image_url");
					//.....
					memberEntity.setNickname(name);
					memberEntity.setGender("m".equals(gender) ? 1 : 0);
					memberEntity.setHeader(header);

				}
			} catch (Exception e) {}

			baseMapper.insert(memberEntity);

			BeanUtils.copyProperties(memberEntity,vo);


			MemberSocialRelationEntity relationEntity = new MemberSocialRelationEntity();
			relationEntity.setSocialUid(uid);
			vo.setSocialUid(uid);
			relationEntity.setAccessToken(accessToken);
			vo.setAccessToken(accessToken);
			relationEntity.setExpiresIn(expiresIn);
			vo.setExpiresIn(expiresIn);
			relationEntity.setMemberId(memberEntity.getId());
			relationEntity.setTokenFrom(tokenFrom);

			memberSocialRelationDao.insert(relationEntity);

		}

		return vo;
	}

}
