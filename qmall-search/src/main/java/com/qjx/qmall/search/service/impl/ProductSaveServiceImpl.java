package com.qjx.qmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.qjx.qmall.common.to.es.SkuEsModel;
import com.qjx.qmall.search.Constant.EsConstant;
import com.qjx.qmall.search.config.QmallElasticSearchConfig;
import com.qjx.qmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ryan
 * 2021-10-27-20:32
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

	@Resource
	RestHighLevelClient client;

	@Override
	public boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException {
		//保存到es
		//1. 给es中建立索引
		BulkRequest bulkRequest = new BulkRequest();
		for (SkuEsModel skuEsModel : skuEsModelList) {
			IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
			indexRequest.id(skuEsModel.getSkuId().toString());
			String s = JSON.toJSONString(skuEsModel);
			indexRequest.source(s, XContentType.JSON);

			bulkRequest.add(indexRequest);

		}

		BulkResponse bulk = client.bulk(bulkRequest, QmallElasticSearchConfig.COMMON_OPTIONS);
		//TODO 如果批量错误
		boolean b = bulk.hasFailures();
		List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());

		log.info("商品上架完成, {}", collect);
		return b;
	}
}
