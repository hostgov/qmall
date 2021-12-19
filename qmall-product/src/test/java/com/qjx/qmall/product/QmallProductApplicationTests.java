package com.qjx.qmall.product;

import com.qjx.qmall.product.dao.AttrGroupDao;
import com.qjx.qmall.product.entity.BrandEntity;
import com.qjx.qmall.product.service.BrandService;
import com.qjx.qmall.product.service.SkuSaleAttrValueService;
import com.qjx.qmall.product.vo.SkuItemSaleAttrVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class QmallProductApplicationTests {

	@Resource
	BrandService brandService;


	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	AttrGroupDao attrGroupDao;

	@Resource
	SkuSaleAttrValueService skuSaleAttrValueService;

	@Test
	public void test() {
//		List<SpuItemAttrGroupVo> attrGroupWithAttrBySpuId = attrGroupDao.getAttrGroupWithAttrBySpuId(100L, 225L);
//		System.out.println(attrGroupWithAttrBySpuId.toString());
		List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(1L);
		System.out.println(saleAttrsBySpuId);

	}


	@Test
	public void testRedis() {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		ops.set("hello", "world_" + UUID.randomUUID().toString());

		String hello = ops.get("hello");
		System.out.println(hello);


	}


	@Test
	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
		brandEntity.setName("报哪个好!!!!!!!");
		brandService.save(brandEntity);
		System.out.println("保存成功.......................");
	}

}
