package com.qjx.qmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.product.dao.SkuInfoDao;
import com.qjx.qmall.product.entity.SkuImagesEntity;
import com.qjx.qmall.product.entity.SkuInfoEntity;
import com.qjx.qmall.product.entity.SpuInfoDescEntity;
import com.qjx.qmall.product.feign.SeckillFeignService;
import com.qjx.qmall.product.service.*;
import com.qjx.qmall.product.vo.SeckillInfoVo;
import com.qjx.qmall.product.vo.SkuItemSaleAttrVo;
import com.qjx.qmall.product.vo.SkuItemVo;
import com.qjx.qmall.product.vo.SpuItemAttrGroupVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {



	@Resource
	SkuImagesService imagesService;

	@Resource
	SpuInfoDescService spuInfoDescService;

	@Resource
	AttrGroupService attrGroupService;

	@Resource
	SkuSaleAttrValueService skuSaleAttrValueService;

	@Resource
	ThreadPoolExecutor executor;

	@Resource
	SeckillFeignService seckillFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
		this.baseMapper.insert(skuInfoEntity);
	}


//	key:
//	catelogId: 0
//	brandId: 0
//	min: 0
//	max: 0
	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params) {

		QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper.and(w -> {
				w.eq("sku_id", key).or().like("sku_name", key);
			});
		}
		String catelogId = (String) params.get("catelogId");
		if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {

			wrapper.eq("catalog_id", catelogId);
		}
		String brandId = (String) params.get("brandId");
		if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
			wrapper.eq("brand_id", brandId);
		}
		String min = (String) params.get("min");
		if (!StringUtils.isEmpty(min)) {
			wrapper.ge("price", min);
		}
		String max = (String) params.get("max");
		if (!StringUtils.isEmpty(max)) {
			try {
				BigDecimal d_max = new BigDecimal(max);
				if (d_max.compareTo(new BigDecimal("0")) == 1) {
					wrapper.le("price",max);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);

	}

	@Override
	public List<SkuInfoEntity> getSkuBySpiId(Long spuId) {
		List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
		return list;
	}


	@Override
	public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
    	SkuItemVo skuItemVo = new SkuItemVo();

		CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
			//1. sku基本信息获取  `pms_sku_info`
			SkuInfoEntity info = getById(skuId);
			skuItemVo.setInfo(info);
			return info;
		}, executor);

		CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((result) -> {
			//3. 获取spu销售属性组合
			List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(result.getSpuId());
			skuItemVo.setSaleAttr(saleAttrVos);
		}, executor);

		CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((result) -> {
			//4. 获取spu的介绍
			SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(result.getSpuId());
			skuItemVo.setDesp(spuInfoDescEntity);
		}, executor);
		CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((result) -> {
			//5. 获取spu的规格参数信息
			List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrBySpuId(result.getSpuId(), result.getCatalogId());
			skuItemVo.setGroupAttrs(attrGroupVos);
		}, executor);


		//2. sku图片信息 `pms_sku_images`
		CompletableFuture<Void> imgFuture = CompletableFuture.runAsync(() -> {
			List<SkuImagesEntity> images = imagesService.getImageBySkuId(skuId);
			skuItemVo.setImages(images);
		}, executor);

		//3.查询当前sku是否参与秒杀优惠
		CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
			R r = seckillFeignService.skuSeckillInfo(skuId);
			if (r.getCode() == 0) {
				SeckillInfoVo seckillInfoVo = r.getData(new TypeReference<SeckillInfoVo>() {
				});
				skuItemVo.setSeckillInfo(seckillInfoVo);
			}
		}, executor);


		//等待所有任务都完成
		CompletableFuture.allOf(saleAttrFuture, baseAttrFuture, imgFuture, seckillFuture)
				.get();

		return skuItemVo;
	}

}
