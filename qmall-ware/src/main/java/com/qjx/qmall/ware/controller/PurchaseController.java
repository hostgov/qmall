package com.qjx.qmall.ware.controller;

import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.ware.entity.PurchaseEntity;
import com.qjx.qmall.ware.service.PurchaseService;
import com.qjx.qmall.ware.vo.MergeVo;
import com.qjx.qmall.ware.vo.PurchaseDoneVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 采购信息
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:11:12
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Resource
    private PurchaseService purchaseService;


    @PostMapping("/done")
    @ApiOperation("模拟采购员完成采购单")
    public R finish(
            @RequestBody
            @ApiParam(value = "采购员提交的采购完成清单", required = true)
                    PurchaseDoneVo doneVo) {
        purchaseService.done(doneVo);
        return R.ok();
    }


    @PostMapping("/received")
    @ApiOperation("模拟采购员领取采购单")
    public R reveived(
            @RequestBody
            @ApiParam(value = "要领取的采购单列表", required = true)
                    List<Long> ids) {
        purchaseService.received(ids);
        return R.ok();
    }



    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo) {
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }


    @GetMapping("/unreceive/list")
    public R unrecieveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnrecieve(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
