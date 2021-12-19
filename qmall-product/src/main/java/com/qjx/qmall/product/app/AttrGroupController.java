package com.qjx.qmall.product.app;

import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.product.entity.AttrEntity;
import com.qjx.qmall.product.entity.AttrGroupEntity;
import com.qjx.qmall.product.service.AttrAttrgroupRelationService;
import com.qjx.qmall.product.service.AttrGroupService;
import com.qjx.qmall.product.service.AttrService;
import com.qjx.qmall.product.service.CategoryService;
import com.qjx.qmall.product.vo.AttrGroupRelationVo;
import com.qjx.qmall.product.vo.AttrGroupWithAttrsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@Api(tags = "属性分组controller")
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Resource
    private AttrGroupService attrGroupService;

    @Resource
    private CategoryService categoryService;

    @Resource
    AttrService attrService;

    @Resource
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    // /product/attrgroup/2/attr/relation

    @ApiOperation("根据分组id查找关联的基本属性(规格参数)")
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelatioin (@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> list = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",list);
    }

    @ApiOperation(value = "查询三级分类对应的所有属性分组及分组下的所有属性")
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs (
            @ApiParam(value = "三级分类id", required = true)
            @PathVariable("catelogId") Long catelogId) {
        // 1. 查出当前分类下的所有属性
        // 2. 查出每个属性分组下的所有属性
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",vos);
    }




    // /1/noattr/relation
    @ApiOperation("根据分组id查找没有分组关联的基本属性(规格参数)")
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelatioin (
            @RequestParam Map<String, Object> params,
            @PathVariable("attrgroupId") Long attrgroupId) {
        PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);
        return R.ok().put("page",page);
    }


    // /attr/relation
    //新增关联关系
    @ApiParam("新增关联关系")
    @PostMapping("/attr/relation")
    public R addRelation(
            @ApiParam(value = "关联关系vo", required = true)
            @RequestBody List<AttrGroupRelationVo> vos) {
        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos) {
        attrService.deleteRelation(vos);
        return R.ok();
    }



    /**
     * 列表
     */
    @ApiOperation("分页查询属性分组信息")
    @GetMapping("/list/{catelogId}")
    public R list(
            @ApiParam(value = "分页请求参数map", required = true)
            @RequestParam Map<String, Object> params,
            @ApiParam(value = "三级分类的Id",required = true)
            @PathVariable Long catelogId){
        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @ApiParam(value = "根据attrGroupId查询分组详情")
    @GetMapping("/info/{attrGroupId}")
    public R info(
            @ApiParam(value = "属性分组Id",required = true)
            @PathVariable("attrGroupId") Long attrGroupId){

		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
