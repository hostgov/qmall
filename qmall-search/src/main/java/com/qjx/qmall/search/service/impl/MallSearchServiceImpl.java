package com.qjx.qmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.qjx.qmall.common.to.es.SkuEsModel;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.search.Constant.EsConstant;
import com.qjx.qmall.search.config.QmallElasticSearchConfig;
import com.qjx.qmall.search.feign.ProductFeignService;
import com.qjx.qmall.search.service.MallSearchService;
import com.qjx.qmall.search.vo.AttrResponseVo;
import com.qjx.qmall.search.vo.BrandVo;
import com.qjx.qmall.search.vo.SearchParam;
import com.qjx.qmall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ryan
 * 2021-11-06-17:08
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

	@Resource
	private RestHighLevelClient restHighLevelClient;

	@Resource
	private ProductFeignService productFeignService;

	@Override
	public SearchResult search(SearchParam param) {
		//1. 动态构建出查询需要DSL语句
		SearchResult searchResult = null;


		// 1. 准备检索请求
		SearchRequest searchRequest = buildSearchRequest(param);
		System.out.println("得到的查询语句是:" + searchRequest.toString());


		try {
			//2. 执行检索请求
			SearchResponse response = restHighLevelClient.search(searchRequest, QmallElasticSearchConfig.COMMON_OPTIONS);
			// 3.分析响应数据封装成我们需要的格式
			searchResult = buildSearchResult(response, param);
//			System.out.println("查询结果是:" + searchResult.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchResult;
	}


	//准备检索请求
	private SearchRequest buildSearchRequest(SearchParam param) {

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		//1  过滤(按照属性)
		// 1) 构建bool -query
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		// 1.1 ) must - 模糊匹配
		if (!StringUtils.isEmpty(param.getKeyword())) {
			boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
		}
		//1.2 bool -filter - 按照三级分类id过滤
		if (param.getCatalog3Id() != null) {
			boolQuery.filter(QueryBuilders.termQuery("catelogId", param.getCatalog3Id()));
		}
		//1.3 bool - filter - 按照品牌id查询
		if (param.getBrandId() != null && param.getBrandId().size() > 0) {
			boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
		}
		//1.4 按照所有指定的属性进行查询
		List<String> attrs = param.getAttrs();
		if (attrs != null && attrs.size() > 0) {


			for (String attrStr : attrs) {
				BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
				String[] s = attrStr.split("_");
				String attrId = s[0];//检索的属性id
				String[] attrValues = s[1].split(":"); // 这个属性的检索用的属性值
				nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
				nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
				NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
				boolQuery.filter(nestedQuery);
			}


		}

		//1.5 bool - filter - 按照是否有库存进行查询
		if (param.getHasStock()!=null) {
			boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
		}

		//1.6 bool - filter - 按照价格区间进行查询
		String skuPriceString = param.getSkuPrice();
		if (!StringUtils.isEmpty(skuPriceString)) {
			RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
			String[] s = skuPriceString.split("_");
			if (s.length == 2) {
				//区间
				rangeQuery.gte(s[0]).lte(s[1]);
			} else if (s.length == 1) {
				if (skuPriceString.startsWith("_")) {
					rangeQuery.lte(s[0]);
				}
				if (skuPriceString.endsWith("_")) {
					rangeQuery.gte(s[0]);
				}
			}

			boolQuery.filter(rangeQuery);
		}


		sourceBuilder.query(boolQuery);

		//2排序,分页,高亮
		//2.1 排序
		String sortStr = param.getSort();
		if (!StringUtils.isEmpty(sortStr)) {
			String[] s = sortStr.split("_");
			SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
			sourceBuilder.sort(s[0], sortOrder);
		}
		//2.2 分页
		sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
		sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

		//2.3 高亮
		if (!StringUtils.isEmpty(param.getKeyword())) {
			HighlightBuilder highlightBuilder = new HighlightBuilder();
			highlightBuilder.field("skuTitle");
			highlightBuilder.preTags("<b style='color:red'>");
			highlightBuilder.postTags("</b>");
			sourceBuilder.highlighter(highlightBuilder);
		}

		//3聚合分析
		//1.品牌聚合
		TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
		brand_agg.field("brandId").size(50);
		// 品牌聚合的子聚合
		brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
		brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
		sourceBuilder.aggregation(brand_agg);
		//2.分类聚合

		TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catelogId").size(20);
		catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catelogName").size(1));
		sourceBuilder.aggregation(catalog_agg);

		//3属性聚合attr_agg
		NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");

		TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");

		attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
		attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));

		attr_agg.subAggregation(attr_id_agg);

		sourceBuilder.aggregation(attr_agg);

		SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
		return searchRequest;

	}


	//构建结果数据
	private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
		SearchResult result = new SearchResult();
		//1. 封装返回的所有查询到的商品
		SearchHits hits = response.getHits();
		SearchHit[] products = hits.getHits();
		List<SkuEsModel> esModels = new ArrayList<>();
		if (products != null && products.length > 0) {
			for (SearchHit hit : products) {
				String sourceAsString = hit.getSourceAsString();
				SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
				if (!StringUtils.isEmpty(param.getKeyword())) {
					HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
					String string = skuTitle.getFragments()[0].string();
					skuEsModel.setSkuTitle(string);
				}

				esModels.add(skuEsModel);
			}
		}
		result.setProducts(esModels);

		//2. 当前所有商品涉及到的所有属性信息
		List<SearchResult.AttrVo> attrVos = new ArrayList<>();
		ParsedNested attr_agg = response.getAggregations().get("attr_agg");
		ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
		for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
			SearchResult.AttrVo attrVo = new SearchResult.AttrVo();


			long attrId = bucket.getKeyAsNumber().longValue();
			attrVo.setAttrId(attrId);
			ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
			String attr_name = attr_name_agg.getBuckets().get(0).getKeyAsString();
			attrVo.setAttrName(attr_name);

			ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
			List<String> attr_values = attr_value_agg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
			attrVo.setAttrValue(attr_values);

			attrVos.add(attrVo);
		}
		result.setAttrs(attrVos);

		//3. 当前商品涉及的品牌信息
		List<SearchResult.BrandVo> brandVos = new ArrayList<>();
		ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
		for (Terms.Bucket bucket : brand_agg.getBuckets()) {
			SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
			brandVo.setBrandId(bucket.getKeyAsNumber().longValue());

			ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
			String brand_img = brand_img_agg.getBuckets().get(0).getKeyAsString();
			brandVo.setBrandImg(brand_img);

			ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
			String brand_name = brand_name_agg.getBuckets().get(0).getKeyAsString();
			brandVo.setBrandName(brand_name);

			brandVos.add(brandVo);
		}
		result.setBrands(brandVos);

		//4. 当前商品涉及的分类信息
		List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
		ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
		List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
		for (Terms.Bucket bucket : buckets) {
			SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
			catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));

			ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
			String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
			catalogVo.setCatalogName(catalog_name);

			catalogVos.add(catalogVo);
		}
		result.setCatalogs(catalogVos);

		//5. 分页信息 页码,总记录数,总页码
		result.setPageNum(param.getPageNum());
		long total = hits.getTotalHits().value;
		result.setTotal(total);
		int totalPages = (int) total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGESIZE : ( (int) total / EsConstant.PRODUCT_PAGESIZE + 1);
		result.setTotalPages(totalPages);
		List<Integer> pageNavs = new ArrayList<>();
		for(int i = 1; i <= totalPages; i++) {
			pageNavs.add(i);
		}
		result.setPageNavs(pageNavs);
		//面包屑导航构建

		if (param.getAttrs() != null && param.getAttrs().size() > 0) {

//		List<SearchResult.AttrVo> attrs = result.getAttrs();
//		Map<Long, String> attrMap = new HashMap<>();
//		for (SearchResult.AttrVo attr : attrs) {
//			attrMap.put(attr.getAttrId(),attr.getAttrName());
//		}
//		List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
//			SearchResult.NavVo navVo = new SearchResult.NavVo();
//			String[] s = attr.split("_");
//			navVo.setNavValue(s[1]);
//			navVo.setNavName(attrMap.get(Long.parseLong(s[0])));

//			try {
//				URLEncoder.encode(attr, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//			String replace = param.get_queryString().replace("&attrs" + attr, "");
//			navVo.setLink("http://search.qmall.com/list.html?" + replace);

//			return navVo;
//		}).collect(Collectors.toList());
//

			List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
				SearchResult.NavVo navVo = new SearchResult.NavVo();
				String[] s = attr.split("_");
				navVo.setNavValue(s[1]);
				R r = productFeignService.attrInfo(Long.parseLong(s[0]));
				result.getAttrIds().add(Long.parseLong(s[0]));
				if (r.getCode() == 0) {
					AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
					});
					navVo.setNavName(data.getAttrName());
				} else {
					navVo.setNavName(s[0]);
				}

				//2.取消了这个面包屑后,我们要跳转到哪个地方,将请求地址的url里的当前置空
				//拿到所有的查询条件,去掉当前
				String replace = replaceQueryString(param, attr, "attrs");
				navVo.setLink("http://search.qmall.com/list.html?" + replace);

				return navVo;
			}).collect(Collectors.toList());


			result.setNavs(navVos);
		}

		//品牌分类面包屑
		if (param.getBrandId() != null && param.getBrandId().size() > 0) {
			List<SearchResult.NavVo> navs = result.getNavs();
			SearchResult.NavVo navVo = new SearchResult.NavVo();

			navVo.setNavName("品牌");
			R r = productFeignService.brandInfo(param.getBrandId());
			if (r.getCode() == 0) {
				List<BrandVo> brands = r.getData("brands", new TypeReference<List<BrandVo>>() {
				});
				StringBuilder buffer = new StringBuilder();
				String replace = "";
				for (BrandVo brand : brands) {
					buffer.append(brand.getName()).append(";");
					replace = replaceQueryString(param, brand.getBrandId() + "", "brandId");
				}
				navVo.setNavValue(buffer.toString());
				navVo.setLink("http://search.qmall.com/list.html?" + replace);
			}
			navs.add(navVo);
		}
		return result;
	}

	private String replaceQueryString(SearchParam param, String value, String key) {
		String encode = null;
		try {
			encode = URLEncoder.encode(value, "UTF-8");
			encode = encode.replace("+", "%20");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String replace = param.get_queryString().replace("&" + key + "=" + encode, "");
		return replace;
	}
}
