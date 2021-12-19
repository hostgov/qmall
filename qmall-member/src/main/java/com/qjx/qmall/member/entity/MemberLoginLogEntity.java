package com.qjx.qmall.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 会员登录记录
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_member_login_log")
@ApiModel(value="MemberLoginLogEntity对象", description="会员登录记录")
public class MemberLoginLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "member_id")
    private Long memberId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "city")
    private String city;

    @ApiModelProperty(value = "登录类型[1-com.qjx.qmall.member.web，2-app]")
    private Integer loginType;


}
