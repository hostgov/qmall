package com.qjx.qmall.product.controller;

import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.product.entity.CategoryEntity;
import com.qjx.qmall.product.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 * 商品三级分类
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 17:23:27
 */
@Api(tags = "商品分类管理")
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类,以树形结构组装起来
     */
    @ApiOperation(value = "商品分类列表树形结构返回")
    @GetMapping("/list/tree")
    public R list(){
        List<CategoryEntity> categoryTreeList = categoryService.listWithTree();

        return R.ok().put("data", categoryTreeList);
    }


    /**
     * 信息
     */
    @ApiOperation("根据分类id查询分类信息")
    @GetMapping("/info/{catId}")
    public R info(
            @ApiParam(value = "分类id", required = true)
            @PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @ApiOperation(value = "新增保存分类信息")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "分类信息", required = true)
            @RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 批量修改
     */
    @ApiOperation(value = "批量修改分类信息")
    @PostMapping("/batchUpdate")
    public R batchUpdate(
            @ApiParam(value = "要修改分类信息数组", required = true)
            @RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));

        return R.ok();
    }

    /**
     * 修改
     */
    @ApiOperation(value = "修改分类信息")
    @PostMapping("/update")
    public R update(
            @ApiParam(value = "分类信息", required = true)
            @RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     */
    @ApiOperation(value = "批量删除指定的商品类别")
    @PostMapping("/delete")
    public R delete(
            @ApiParam(value = "商品类别id数组", required = true)
            @RequestBody Long[] catIds){
		//categoryService.removeByIds(Arrays.asList(catIds));

        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
