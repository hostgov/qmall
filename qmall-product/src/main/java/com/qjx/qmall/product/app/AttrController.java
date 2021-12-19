package com.qjx.qmall.product.app;

import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.product.entity.ProductAttrValueEntity;
import com.qjx.qmall.product.service.AttrService;
import com.qjx.qmall.product.service.ProductAttrValueService;
import com.qjx.qmall.product.vo.AttrRespVo;
import com.qjx.qmall.product.vo.AttrVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品属性
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@Api(tags = "商品属性管理")
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Resource
    private AttrService attrService;

    @Resource
    ProductAttrValueService productAttrValueService;


    @ApiOperation("查询spu的规格")
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrlistforspu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrlistforspu(spuId);
        return R.ok().put("data",entities);
    }


    @ApiOperation("按三级分类id查询其有的商品属性列表+对应属性分组")
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseList(
            @ApiParam(value = "分页参数", required = true)
            @RequestParam Map<String, Object> params,
            @ApiParam(value = "三级分类Id", required = true)
            @PathVariable("catelogId") Long catelogId,
            @ApiParam(value = "base基本属性或者sale销售属性", required = true)
            @PathVariable("attrType") String type
    ){
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, type);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @ApiOperation("商品属性列表")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRespVo respVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @ApiOperation("新增属性")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "attrVo", required = true)
            @RequestBody AttrVo attr){
		attrService.saveAttrVo(attr);

        return R.ok();
    }




    /**
     * 修改
     */
    @ApiOperation("修改属性")
    @PostMapping("/update")
    public R update(
            @ApiParam(value = "attrVo", required = true)
            @RequestBody AttrVo attrVo){
		attrService.updateAttr(attrVo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
