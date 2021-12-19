package com.qjx.qmall.ware.controller;

import com.qjx.qmall.common.exception.BizCodeEnum;
import com.qjx.qmall.common.exception.NoStockException;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.ware.entity.WareSkuEntity;
import com.qjx.qmall.ware.service.WareSkuService;
import com.qjx.qmall.ware.vo.SkuHasStockVo;
import com.qjx.qmall.ware.vo.WareSkuLockVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品库存
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:11:12
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Resource
    private WareSkuService wareSkuService;


    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo) {
        Boolean stockResult = null;
        try {
            stockResult = wareSkuService.orderLockStock(vo);
            return R.ok();
        } catch (NoStockException e) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
        }

    }


    // 查询sku是否有库存
    @PostMapping("/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> vos = wareSkuService.getSkusHasStock(skuIds);

        return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
