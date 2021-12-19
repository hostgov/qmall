package com.qjx.qmall.product.app;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.product.entity.BrandEntity;
import com.qjx.qmall.product.entity.CategoryBrandRelationEntity;
import com.qjx.qmall.product.service.CategoryBrandRelationService;
import com.qjx.qmall.product.vo.BrandVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@RestController
@Api(tags = "分类与品牌的关联")
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @ApiOperation(value = "分类与品牌关联表列表信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    @ApiOperation("根据三级分类id获取关联的品牌列表")
    @GetMapping("/brands/list")
    public R relationBrandsList (
            @ApiParam(value = "三级分类id", required = true)
            @RequestParam(value = "catId", required = true) Long catId) {

        List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandsByCatId(catId);

        List<BrandVo> collect = brandEntities.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data",collect);
    }

    /**
     * 列表
     */
    @ApiOperation(value = "根据品牌ID查询此品牌关联的所有分类信息")
    @GetMapping("/catelog/list")
    public R catelogList(
            @ApiParam(value = "品牌id", required = true)
            @RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>()
                .eq("brand_id", brandId)
        );
        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存品牌与分类关系信息")
    public R save(
            @ApiParam(value = "品牌与分类关系,必须有brandId,catelogId", required = true)
            @RequestBody CategoryBrandRelationEntity categoryBrandRelation){

		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
