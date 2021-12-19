package com.qjx.qmall.search.service;

import com.qjx.qmall.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * Ryan
 * 2021-10-27-20:31
 */
public interface ProductSaveService {

	boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException;
}
