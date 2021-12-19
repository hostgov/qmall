package com.qjx.qmall.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 会员
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@ToString
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberEntity implements Serializable {

    private Long id;

    private Long levelId;

    private String username;

    private String password;

    private String nickname;

    private String mobile;

    private String email;

    private String header;

    private Integer gender;

    private Date birth;

    private String city;

    private String job;

    private String sign;

    private Integer sourceType;

    private Integer integration;

    private Integer growth;

    private Integer status;

    private Date createTime;


}
