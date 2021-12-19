package com.qjx.qmall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Ryan
 * 2021-11-02-14:44
 */

//二级分类vo
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {

	private String catalog1Id; //一级父分类id

	private List<Catelog3Vo> catalog3List; //三级子分类

	private String id;

	private  String name;


	//三级分类vo
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Catelog3Vo {
		private String catalog2Id; //父分类,2级分类id

		private String id;

		private String name;
	}
}
