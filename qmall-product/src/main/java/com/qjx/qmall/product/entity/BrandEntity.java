package com.qjx.qmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qjx.qmall.common.valid.AddGroup;
import com.qjx.qmall.common.valid.ListedValues;
import com.qjx.qmall.common.valid.UpdateGroup;
import com.qjx.qmall.common.valid.UpdateStatusGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * <p>
 * 品牌
 * </p>
 *
 * @author Hostgov
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pms_brand")
@ApiModel(value="BrandEntity对象", description="品牌")
public class BrandEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "品牌id")
    @TableId(value = "brand_id", type = IdType.AUTO)
    @NotNull(message = "修改必须指定品牌id", groups = {UpdateGroup.class, UpdateStatusGroup.class})
    @Null(message = "新增不能指定id", groups = {AddGroup.class})
    private Long brandId;

    @ApiModelProperty(value = "品牌名")
    @NotBlank(message = "品牌名必须提交", groups = {UpdateGroup.class, AddGroup.class})
    private String name;

    @NotBlank(groups = {AddGroup.class})
    @URL(message = "logo必须是合法的url地址", groups = {UpdateGroup.class, AddGroup.class})
    @ApiModelProperty(value = "品牌logo地址")
    private String logo;

    @ApiModelProperty(value = "介绍")
    private String descript;

    @ApiModelProperty(value = "显示状态[0-不显示；1-显示]")
    @NotNull(groups = {AddGroup.class, UpdateStatusGroup.class})
    @ListedValues(vals={0, 1}, groups = {AddGroup.class, UpdateStatusGroup.class})
    private Integer showStatus;

    @NotEmpty(groups = {AddGroup.class})
    @ApiModelProperty(value = "检索首字母")
    @Pattern(regexp="^[A-Za-z]$", message = "检索首字母必须是一个字母", groups = {AddGroup.class, UpdateGroup.class})
    private String firstLetter;

    @NotNull(groups = {AddGroup.class})
    @ApiModelProperty(value = "排序")
    @Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
    private Integer sort;


}
