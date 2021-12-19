package com.qjx.qmall.product.app;

import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.product.entity.SpuInfoEntity;
import com.qjx.qmall.product.service.SpuInfoService;
import com.qjx.qmall.product.vo.SpuSaveVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;



/**
 * spu信息
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Resource
    private SpuInfoService spuInfoService;

    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId){
        spuInfoService.up(spuId);

        return R.ok();
    }

    @GetMapping("/skuId/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id") Long skuId) {
        SpuInfoEntity entity = spuInfoService.getSpuInfoBySkuId(skuId);
        return R.ok().setData(entity);
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo vo){

        spuInfoService.saveSpuInfo(vo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
