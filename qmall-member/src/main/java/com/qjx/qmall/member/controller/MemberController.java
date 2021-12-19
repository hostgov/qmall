package com.qjx.qmall.member.controller;

import com.qjx.qmall.common.exception.BizCodeEnum;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.member.entity.MemberEntity;
import com.qjx.qmall.member.exception.PhoneExistException;
import com.qjx.qmall.member.exception.UserNameExistException;
import com.qjx.qmall.member.feign.CouponFeignService;
import com.qjx.qmall.member.service.MemberService;
import com.qjx.qmall.member.vo.MemberEntityWithSocialVo;
import com.qjx.qmall.member.vo.MemberLoginVo;
import com.qjx.qmall.member.vo.MemberRegistVo;
import com.qjx.qmall.member.vo.WeiboTokenResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:09:10
 */
@Api(tags = "会员")
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Resource
    private MemberService memberService;

    @Resource
    CouponFeignService couponFeignService;


    @PostMapping("/weibo/oauth2/login")
    public R WeiboOauthLogin(@RequestBody WeiboTokenResponse weiboTokenResponse) throws Exception {
        MemberEntityWithSocialVo memberEntity = memberService.login(weiboTokenResponse);
        if (memberEntity != null) {

            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getCode(),BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
    }


    @PostMapping("/regist")
    public R memberRegister(@RequestBody MemberRegistVo vo) {

        try {
            memberService.regist(vo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UserNameExistException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(),BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo) {
        MemberEntityWithSocialVo memberEntity = memberService.login(vo);
        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getCode(),BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
    }


    @ApiOperation(value = "测试用会员调取coupon服务")
    @GetMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R r = couponFeignService.memberCoupons();
        return R.ok().put("member", memberEntity)
                .put("coupon", r.get("coupons"));

    }


    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
