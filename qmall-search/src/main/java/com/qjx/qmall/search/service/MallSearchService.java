package com.qjx.qmall.search.service;

import com.qjx.qmall.search.vo.SearchParam;
import com.qjx.qmall.search.vo.SearchResult;

/**
 * Ryan
 * 2021-11-06-17:07
 */
public interface MallSearchService {

	//检索
	SearchResult search(SearchParam param);
}
