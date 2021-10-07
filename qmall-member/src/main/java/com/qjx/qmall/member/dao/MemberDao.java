package com.qjx.qmall.member.dao;

import com.qjx.qmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:09:10
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
