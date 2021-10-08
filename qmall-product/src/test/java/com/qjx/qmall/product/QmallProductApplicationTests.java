package com.qjx.qmall.product;

import com.qjx.qmall.product.entity.BrandEntity;
import com.qjx.qmall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class QmallProductApplicationTests {

	@Resource
	BrandService brandService;

	@Test
	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
		brandEntity.setName("报哪个好!!!!!!!");
		brandService.save(brandEntity);
		System.out.println("保存成功.......................");
	}

}
