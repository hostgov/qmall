package com.qjx.qmall.search;

import com.alibaba.fastjson.JSON;
import com.qjx.qmall.search.config.QmallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
class QmallSearchApplicationTests {

	@Resource
	RestHighLevelClient client;

	@Data
	@ToString
	static class Account {

		private int account_number;
		private int balance;
		private String firstname;
		private String lastname;
		private int age;
		private String gender;
		private String address;
		private String employer;
		private String email;
		private String city;
		private String state;
	}


	@Test
	void contextLoads() {
		System.out.println(client);
	}

	@Test
	void indexData() throws IOException {
		IndexRequest indexRequest = new IndexRequest("users");
		indexRequest.id("1");
//		indexRequest.source("userName","zhangsan","age",18,"gender", "男");
		User user = new User();
		user.setUserName("zhangsan");
		user.setAge(18);
		user.setGender("男");
		String jsonString = JSON.toJSONString(user);
		indexRequest.source(jsonString, XContentType.JSON);


		IndexResponse response = client.index(indexRequest, QmallElasticSearchConfig.COMMON_OPTIONS);

		System.out.println(response);


	}
	@Data
	class User {
		private String userName;
		private String gender;
		private Integer age;
	}


	@Test
	void searchData() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("bank");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// 构造检索条件
		searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

		TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
		searchSourceBuilder.aggregation(ageAgg);

		AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
		searchSourceBuilder.aggregation(balanceAvg);


		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = client.search(searchRequest, QmallElasticSearchConfig.COMMON_OPTIONS);

		System.out.println(searchResponse.toString());

//		JSON.parseObject(searchResponse.toString(), Map.class);
		//分析结果
		SearchHits hits = searchResponse.getHits();
		for(SearchHit hit :hits.getHits()) {
			String sourceAsString = hit.getSourceAsString();
			Account account = JSON.parseObject(sourceAsString, Account.class);
			System.out.println("account:" + account);

		}
		// 获取检索到的分析信息(聚合信息)
		Aggregations aggregations = searchResponse.getAggregations();
//		for (Aggregation aggregation : aggregations.asList()) {
//			System.out.println("当前聚合: " + aggregation.getName());
//		}
		Terms ageAgg1 = aggregations.get("ageAgg");
		for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
			String keyAsString = bucket.getKeyAsString();
			System.out.println("年龄: " + keyAsString + "有" + bucket.getDocCount() + "个人");
		}
		Avg balanceAvg1 = aggregations.get("balanceAvg");
		System.out.println("平均薪资 : " + balanceAvg1.getValue());
	}

}
