package com.qjx.qmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.ware.entity.PurchaseEntity;
import com.qjx.qmall.ware.vo.MergeVo;
import com.qjx.qmall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:11:12
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

	PageUtils queryPageUnrecieve(Map<String, Object> params);

	void mergePurchase(MergeVo mergeVo);

	void received(List<Long> ids);

	void done(PurchaseDoneVo doneVo);
}

