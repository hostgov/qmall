package com.qjx.qmall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.constant.WareConstant;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.ware.dao.PurchaseDao;
import com.qjx.qmall.ware.entity.PurchaseDetailEntity;
import com.qjx.qmall.ware.entity.PurchaseEntity;
import com.qjx.qmall.ware.service.PurchaseDetailService;
import com.qjx.qmall.ware.service.PurchaseService;
import com.qjx.qmall.ware.service.WareSkuService;
import com.qjx.qmall.ware.vo.MergeVo;
import com.qjx.qmall.ware.vo.PurchaseDoneVo;
import com.qjx.qmall.ware.vo.PurchaseItemDoneVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

	@Resource
	PurchaseDetailService detailService;

	@Resource
	WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public PageUtils queryPageUnrecieve(Map<String, Object> params) {
		QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("status",0).or().eq("status",1);
		IPage<PurchaseEntity> page = this.page(
				new Query<PurchaseEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);

	}

	@Override
	@Transactional
	public void mergePurchase(MergeVo mergeVo) {
		Long purchaseId = mergeVo.getPurchaseId();
		if (purchaseId == null) {
			PurchaseEntity purchaseEntity = new PurchaseEntity();
			purchaseEntity.setCreateTime(new Date());
			purchaseEntity.setUpdateTime(new Date());
			purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
			this.save(purchaseEntity);

			purchaseId = purchaseEntity.getId();
		}
		List<Long> items = mergeVo.getItems();
		Long finalPurchaseId = purchaseId;
		List<PurchaseDetailEntity> collect = items.stream().map(item -> {
			PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
			detailEntity.setId(item);
			detailEntity.setPurchaseId(finalPurchaseId);
			detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
			return detailEntity;
		}).collect(Collectors.toList());

		detailService.updateBatchById(collect);

		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(purchaseId);
		purchaseEntity.setUpdateTime(new Date());
		this.updateById(purchaseEntity);
	}

	@Override
	@Transactional
	public void received(List<Long> ids) {
		//1. 确认当前采购单是新建或者已分配状态
		List<PurchaseEntity> collect = ids.stream().map(id -> {
			PurchaseEntity entity = this.getById(id);
			return entity;
		}).filter(item -> item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
				item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()).peek(item -> item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode()))
				.collect(Collectors.toList());
		//2.改变采购单的状态
		this.updateBatchById(collect);


		//3.改变采购项的状态
		collect.forEach(item -> {
			List<PurchaseDetailEntity> entities = detailService.listDetailByPurchaseId(item.getId());
			List<PurchaseDetailEntity> detailEntities = entities.stream().map(entity -> {
				PurchaseDetailEntity entity1 = new PurchaseDetailEntity();
				entity1.setId(entity.getId());
				entity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
				return entity1;
			}).collect(Collectors.toList());
			detailService.updateBatchById(detailEntities);
		});
	}

	@Transactional
	@Override
	public void done(PurchaseDoneVo doneVo) {

		Long id = doneVo.getId();


		//2. 改变采购项状态
		boolean flag = true;
		List<PurchaseItemDoneVo> items = doneVo.getItems();

		ArrayList<PurchaseDetailEntity> updates = new ArrayList<>();
		for (PurchaseItemDoneVo item: items) {
			PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();

			if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
				flag = false;
				detailEntity.setStatus(item.getStatus());
			} else {
				detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());

				//3. 将成功采购进行入库
				PurchaseDetailEntity doneDetailEntity = detailService.getById(item.getItemId());
				wareSkuService.addStore(doneDetailEntity.getSkuId(), doneDetailEntity.getWareId(), doneDetailEntity.getSkuNum());
			}
			detailEntity.setId(item.getItemId());

			updates.add(detailEntity);
		}
		detailService.updateBatchById(updates);

		//1. 改变采购单状态
		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(id);
		purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
		purchaseEntity.setUpdateTime(new Date());
		this.updateById(purchaseEntity);


	}

}
