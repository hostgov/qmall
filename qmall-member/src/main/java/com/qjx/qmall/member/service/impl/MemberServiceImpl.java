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
		//设置默认等级
		MemberLevelEntity memberDefaultLevel = memberLevelDao.getDefaultLevel();
		entity.setLevelId(memberDefaultLevel.getId());
		//检查用户名和手机号是否唯一,为了让controller能感知异常,使用异常机制
		checkUsernameUnique(vo.getUsername());
		checkPhoneUnique(vo.getPhone());

		entity.setMobile(vo.getPhone());
		entity.setUsername(vo.getUsername());

		entity.setNickname(vo.getUsername());

		//密码要进行加密存储
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encode = passwordEncoder.encode(vo.getPassword());
		entity.setPassword(encode);

		//其他默认信息


		//保存
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
		//去数据库查询
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
    	//登录和注册合并逻辑
		String uid = weiboTokenResponse.getUid();

		String tokenFrom = weiboTokenResponse.getTokenFrom();

		String accessToken = weiboTokenResponse.getAccessToken();

		long expiresIn = weiboTokenResponse.getExpiresIn();

		MemberEntityWithSocialVo vo = new MemberEntityWithSocialVo();
		//判断当前社交用户是否已经注册过
		MemberSocialRelationEntity memberSocialRelationEntity = memberSocialRelationDao.selectOne(new QueryWrapper<MemberSocialRelationEntity>().eq("social_uid", uid).eq("token_from", tokenFrom));
		if (memberSocialRelationEntity != null) {
			//这个用户已经注册
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
			//2. 没有查到当前社交用户对应的记录,需要注册一个
			MemberEntity memberEntity = new MemberEntity();
			//查询当前社交用户的社交帐号信息(昵称,性别等)

			try {
				Map<String, String> query = new HashMap<>();
				query.put("access_token", accessToken);
				query.put("uid",uid);
				HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
				if (response.getStatusLine().getStatusCode() == 200) {
					String json = EntityUtils.toString(response.getEntity());
					JSONObject jsonObject = JSON.parseObject(json);
					String name = jsonObject.getString("name"); //昵称
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
