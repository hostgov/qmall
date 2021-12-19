package com.qjx.qmall.product.app;

import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.common.valid.AddGroup;
import com.qjx.qmall.common.valid.UpdateGroup;
import com.qjx.qmall.common.valid.UpdateStatusGroup;
import com.qjx.qmall.product.entity.BrandEntity;
import com.qjx.qmall.product.service.BrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 品牌
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@RestController
@Api(tags = "品牌表")
@RequestMapping("product/brand")
public class BrandController {
    @Resource
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("/infos")
    public R info (@RequestParam("brandIds") List<Long> brandIds) {
        List<BrandEntity> brands = brandService.getBrandsByIds(brandIds);
        return R.ok().put("brands", brands);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @ApiOperation("新增品牌信息")
    @PostMapping("/save")
    public R save(
            @Validated(AddGroup.class)
            @ApiParam(value = "品牌对象", required = true)
            @RequestBody BrandEntity brand){
//        if (result.hasErrors()) {
//            Map<String, String> map = new HashMap<>();
//            result.getFieldErrors().forEach(item -> {
//                String message = item.getDefaultMessage();
//                String field = item.getField();
//            });
//            R.error(400, "提交的数据不合法").put("data", map);
//        }
		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @Transactional
    @ApiOperation(value = "修改品牌信息")
    @PostMapping("/update")
    public R update(
            @ApiParam(value = "品牌对象必须有品牌id",required = true)
            @Validated(UpdateGroup.class)
            @RequestBody BrandEntity brand){
		brandService.updateDetailById(brand);

        return R.ok();
    }

    /**
     * 修改show_status
     */
    @ApiOperation(value = "修改品牌信息中的showStatus")
    @PostMapping("/update/status")
    public R updateStatus(
            @ApiParam(value = "品牌信息对象,有且只有showStatus和brandId",required = true)
            @Validated(UpdateStatusGroup.class)
            @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
