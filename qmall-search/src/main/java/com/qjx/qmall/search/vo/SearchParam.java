package com.qjx.qmall.search.vo;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Ryan
 * 2021-11-06-17:05
 */
@Validated
@Data
public class SearchParam {


	private String keyword;     //检索关键字
	private Long catalog3Id;    //三级分类id

	/*
	    sort=saleCount_asc/desc
	    sort=skuPrice_asc/desc
	    sort=hotScore_asc/desc
	 */
	private String sort;// 排序条件

	/*
		过滤条件
		hasStock, skuPrice区间, brandId, catalog3Id, attrs
		hasStock=0/1
		skuPrice=1_500/_500/500_

	 */
	private Integer hasStock; //是否只显示有货 0 (无库存) 1 (有库存))
	private String skuPrice; //价格区间
	private List<Long> brandId; // 品牌id,可以多选
	private List<String> attrs; //按照属性筛选

	private Integer pageNum = 1; // 页码


	//原生的所有查询条件
	private String _queryString;
}
