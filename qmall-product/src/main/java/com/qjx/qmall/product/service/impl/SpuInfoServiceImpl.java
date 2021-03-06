package com.qjx.qmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.constant.ProductConstant;
import com.qjx.qmall.common.to.SkuHasStockVo;
import com.qjx.qmall.common.to.SkuReductionTo;
import com.qjx.qmall.common.to.SpuBoundsTo;
import com.qjx.qmall.common.to.es.SkuEsModel;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.product.dao.SpuInfoDao;
import com.qjx.qmall.product.entity.*;
import com.qjx.qmall.product.feign.CouponFeignService;
import com.qjx.qmall.product.feign.SearchFeignService;
import com.qjx.qmall.product.feign.WareFeignService;
import com.qjx.qmall.product.service.*;
import com.qjx.qmall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


	@Resource
	SpuInfoDescService spuInfoDescService;

	@Resource
	SpuImagesService imagesService;

	@Resource
	AttrService attrService;

	@Resource
	ProductAttrValueService productAttrValueService;

	@Resource
	SkuInfoService skuInfoService;

	@Resource
	SkuImagesService skuImagesService;

	@Resource
	SkuSaleAttrValueService skuSaleAttrValueService;

	@Resource
	CouponFeignService couponFeignService;

	@Resource
	BrandService brandService;

	@Resource
	CategoryService categoryService;

	@Resource
	WareFeignService wareFeignService;

	@Resource
	SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
	@Override
	public void saveSpuInfo(SpuSaveVo vo) {
		//1. ??????spu???????????? `pms_spu_info`
	    SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
	    BeanUtils.copyProperties(vo, spuInfoEntity);
	    spuInfoEntity.setCreateTime(new Date());
	    spuInfoEntity.setUpdateTime(new Date());
	    this.saveBaseSpuInfo(spuInfoEntity);

	    //2. ??????spu??????????????? `pms_spu_info_desc`
	    List<String> decript = vo.getDecript();
	    SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
	    descEntity.setSpuId(spuInfoEntity.getId());
	    descEntity.setDecript(String.join(",", decript));
	    spuInfoDescService.saveSpuInfoDesc(descEntity);

	    //3. ??????spu???????????? `pms_spu_images`
	    List<String> images = vo.getImages();
	    imagesService.saveImages(spuInfoEntity.getId(),images);

	    //4. ??????spu??????????????? `pms_product_attr_value`
	    List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
	    List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
		    ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
		    productAttrValueEntity.setAttrId(attr.getAttrId());

		    AttrEntity attrEntity = attrService.getById(attr.getAttrId());
		    productAttrValueEntity.setAttrName(attrEntity.getAttrName());

		    productAttrValueEntity.setAttrValue(attr.getAttrValues());
		    productAttrValueEntity.setQuickShow(attr.getShowDesc());
		    productAttrValueEntity.setSpuId(spuInfoEntity.getId());

		    return productAttrValueEntity;
	    }).collect(Collectors.toList());
	    productAttrValueService.saveProductAttr(collect);

	    //5. ??????spu??????????????? `sms_spu_bounds`
	    Bounds bounds = vo.getBounds();
	    SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
	    BeanUtils.copyProperties(bounds,spuBoundsTo);
	    spuBoundsTo.setSpuId(spuInfoEntity.getId());
	    R r = couponFeignService.saveSpuBounds(spuBoundsTo);
	    if (r.getCode() != 0) {
	    	log.error("????????????spu??????????????????");
	    }

	    //6. ????????????spu?????????sku???????????????
	    List<Skus> skus = vo.getSkus();

	    if (skus != null && skus.size() > 0) {
	    	skus.forEach(item -> {
	    		String defaultImg = "";
			    for (Images image : item.getImages()) {
				    if (image.getDefaultImg() == 1) {
				    	defaultImg = image.getImgUrl();
				    }
			    }

			    SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
			    BeanUtils.copyProperties(item, skuInfoEntity);
			    skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
			    skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
			    skuInfoEntity.setSaleCount(0L);
			    skuInfoEntity.setSpuId(spuInfoEntity.getId());

			    skuInfoEntity.setSkuDefaultImg(defaultImg);
			    // 6.1 ??????sku??????????????? `pms_sku_info`
			    skuInfoService.saveSkuInfo(skuInfoEntity);

			    Long skuId = skuInfoEntity.getSkuId();

			    List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
				    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
					skuImagesEntity.setSkuId(skuId);
					skuImagesEntity.setImgUrl(img.getImgUrl());
					skuImagesEntity.setDefaultImg(img.getDefaultImg());
				    return skuImagesEntity;
			    }).filter( image -> !StringUtils.isEmpty(image.getImgUrl())).collect(Collectors.toList());
			    // 6.2 ??????sku??????????????? `pms_sku_images`
			    //
			    skuImagesService.saveBatch(imagesEntities);

			    List<Attr> attrs = item.getAttr();
			    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
				    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
				    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
				    skuSaleAttrValueEntity.setSkuId(skuId);
				    return skuSaleAttrValueEntity;
			    }).collect(Collectors.toList());
			    // 6.3 ??????sku?????????????????? `pms_sku_sale_attr_value`
			    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

			    // 6.4 ??????sku????????????????????????
			    SkuReductionTo skuReductionTo = new SkuReductionTo();
			    BeanUtils.copyProperties(item, skuReductionTo);
			    skuReductionTo.setSkuId(skuId);
			    if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
				    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
				    if (r1.getCode() != 0) {
					    log.error("????????????sku??????????????????");
				    }
			    }


		    });
	    }



	            //6.4.1 `sms_sku_ladder`
	            //6.4.2 `sms_sku_full_reduction`
	            //6.4.3 `sms_member_price`

	}

	@Override
	public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
		this.baseMapper.insert(spuInfoEntity);
	}

	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params) {
		QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper.and((w) -> {
				w.eq("id", key).or().like("spu_name",key);
			});
		}

		String status = (String) params.get("status");
		if (!StringUtils.isEmpty(status)) {
			wrapper.eq("publish_status", status);
		}

		String brandId = (String) params.get("brandId");
		if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
			wrapper.eq("brand_id", brandId);
		}

		String catelogId = (String) params.get("catelogId");
		if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
			wrapper.eq("catalog_id", catelogId);
		}


		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	//????????????
	@Override
	public void up(Long spuId) {

		//1. ????????????spuId???????????????sku??????,??????????????????
		List<SkuInfoEntity> skus = skuInfoService.getSkuBySpiId(spuId);
		List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

		//????????????spu?????????????????????????????????
		List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrlistforspu(spuId);
		List<Long> attrsIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());

		List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrsIds);
		Set<Long> idSet = new HashSet<>(searchAttrIds);


		List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> idSet.contains(item.getAttrId())).map(item -> {
			SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
			BeanUtils.copyProperties(item, attrs1);
			return attrs1;
		}).collect(Collectors.toList());

		// ?????????????????????????????????????????????
		Map<Long, Boolean> stockMap = null;
		try {
			R r = wareFeignService.getSkusHasStock(skuIds);

			stockMap = r.getData(new TypeReference<List<SkuHasStockVo>>(){}).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
		} catch (Exception e) {
			log.error("????????????????????????, ??????{}", e);
		}

		//2. ??????sku??????
		Map<Long, Boolean> finalStockMap = stockMap;
		List<SkuEsModel> upProducts = skus.stream().map(sku -> {
			// ????????????
			SkuEsModel skuEsModel = new SkuEsModel();
			BeanUtils.copyProperties(sku,skuEsModel);
			skuEsModel.setSkuPrice(sku.getPrice());
			skuEsModel.setSkuImg(sku.getSkuDefaultImg());
			skuEsModel.setCatelogId(sku.getCatalogId());


			//?????????????????????
			if (finalStockMap == null) {
				skuEsModel.setHasStock(true);
			} else {

				skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
			}

			// ????????????,0
			skuEsModel.setHotScore(0L);

			//?????? brandName, brandImg,catelogName
			BrandEntity brandEntity = brandService.getById(skuEsModel.getBrandId());
			skuEsModel.setBrandName(brandEntity.getName());
			skuEsModel.setBrandImg(brandEntity.getLogo());

			CategoryEntity categoryEntity = categoryService.getById(skuEsModel.getCatelogId());
			skuEsModel.setCatelogName(categoryEntity.getName());

			//??????????????????
			skuEsModel.setAttrs(attrsList);


			return skuEsModel;

		}).collect(Collectors.toList());

		//5. ??????????????????es???????????? qmall-search
		R r = searchFeignService.productStatusUp(upProducts);
		Integer code = r.getCode();
		if (code == 0) {
			//??????????????????
			//TODO ????????????spu?????????
			baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
		} else {
			//??????????????????
			//TODO ????????????,???????????????,????????????
		}
	}

	@Override
	public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
		SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
		Long spuId = skuInfoEntity.getSpuId();
		SpuInfoEntity spuInfoEntity = getById(spuId);
		return spuInfoEntity;
	}


}
