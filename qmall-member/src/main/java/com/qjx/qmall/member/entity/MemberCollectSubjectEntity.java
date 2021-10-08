package com.qjx.qmall.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 会员收藏的专题活动
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_member_collect_subject")
@ApiModel(value="MemberCollectSubjectEntity对象", description="会员收藏的专题活动")
public class MemberCollectSubjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "subject_id")
    private Long subjectId;

    @ApiModelProperty(value = "subject_name")
    private String subjectName;

    @ApiModelProperty(value = "subject_img")
    private String subjectImg;

    @ApiModelProperty(value = "活动url")
    private String subjectUrll;


}
