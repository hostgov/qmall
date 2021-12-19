package com.qjx.qmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.ware.dao.WareInfoDao;
import com.qjx.qmall.ware.entity.WareInfoEntity;
import com.qjx.qmall.ware.feign.MemberFeignService;
import com.qjx.qmall.ware.service.WareInfoService;
import com.qjx.qmall.ware.vo.FareVo;
import com.qjx.qmall.ware.vo.MemberAddressVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

	@Resource
	MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("id", key)
                    .or().like("name", key)
                    .or().like("address", key)
                    .or().like("areacode", key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

	@Override
	public FareVo getFare(Long addrId) {
		FareVo fareVo = new FareVo();
		R r = memberFeignService.addrInfo(addrId);
		MemberAddressVo data = r.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {
		});
		if (data != null) {
			//结合物流第三方接口算运费
			String phone = data.getPhone();
			String substring = phone.substring(phone.length() - 2, phone.length() - 1);
			BigDecimal fare = new BigDecimal(substring);
			fareVo.setAddress(data);
			fareVo.setFare(fare);
		}

		return fareVo;
	}

}
