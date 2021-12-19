package com.qjx.qmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:09:10
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

