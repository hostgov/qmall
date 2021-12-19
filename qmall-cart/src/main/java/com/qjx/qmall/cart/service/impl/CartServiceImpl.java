package com.qjx.qmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.qjx.qmall.cart.feign.ProductFeignService;
import com.qjx.qmall.cart.interceptor.CartInterceptor;
import com.qjx.qmall.cart.service.CartService;
import com.qjx.qmall.cart.vo.Cart;
import com.qjx.qmall.cart.vo.CartItem;
import com.qjx.qmall.cart.vo.SkuInfoVo;
import com.qjx.qmall.cart.vo.UserInfoTo;
import com.qjx.qmall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Ryan
 * 2021-11-18-21:24
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	ProductFeignService productFeignService;

	@Resource
	ThreadPoolExecutor executor;


	private final String CART_PREFIX = "qmall:cart:";

	@Override
	public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
		BoundHashOperations<String, Object, Object> cartOps = getCartOperations();


		String result = (String) cartOps.get(skuId.toString());
		CartItem cartItem;
		if (StringUtils.isEmpty(result)) {
			//购物车还没有此商品
			cartItem = new CartItem();
			//添加新商品到购物车
			CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
				R skuInfo = productFeignService.info(skuId);
				SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
				});
				cartItem.setCheck(true);
				cartItem.setCount(num);
				cartItem.setImage(data.getSkuDefaultImg());
				cartItem.setTitle(data.getSkuTitle());
				cartItem.setSkuId(skuId);
				cartItem.setPrice(data.getPrice());

			}, executor);


			//远程查询sku的属性组合信息
			CompletableFuture<Void> getSkuSaleAttrValuesFuture = CompletableFuture.runAsync(() -> {
				List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
				cartItem.setSkuAttr(values);
			}, executor);

			CompletableFuture.allOf(getSkuInfoFuture,getSkuSaleAttrValuesFuture).get();

			String s = JSON.toJSONString(cartItem);
			cartOps.put(skuId.toString(), s);
		}
		else {
			//购物车有此商品,修改数量
			cartItem = JSON.parseObject(result, CartItem.class);
			cartItem.setCount(cartItem.getCount() + num);

			cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));

		}
		return cartItem;
	}

	@Override
	public CartItem getCartItem(Long skuId) {

		BoundHashOperations<String, Object, Object> ops = getCartOperations();
		String item = (String) ops.get(skuId.toString());
		return JSON.parseObject(item, CartItem.class);
	}

	@Override
	public Cart getCart() throws ExecutionException, InterruptedException {
		Cart cart = new Cart();
		UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
		if (userInfoTo.getUserId() != null) {
			//1.登录
			String cartKey = CART_PREFIX + userInfoTo.getUserId();
			//如果临时购物车的数据还没有进行合并
			String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
			List<CartItem> tempCartItems = getCartItems(tempCartKey);
			if (tempCartItems != null) {
				//临时购物车有数据,进行合并
				for (CartItem tempCartItem : tempCartItems) {
					addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
				}
				clearCart(tempCartKey);
			}
			//获取登录后的购物车
			List<CartItem> cartItems = getCartItems(cartKey);
			cart.setItems(cartItems);


		} else {
			String cartKey = CART_PREFIX + userInfoTo.getUserKey();
			cart.setItems(getCartItems(cartKey));

		}
		return cart;
	}

	@Override
	public void clearCart(String cartKey) {
		stringRedisTemplate.delete(cartKey);
	}

	@Override
	public void checkItem(Long skuId, Integer check) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOperations();
		CartItem cartItem = getCartItem(skuId);
		cartItem.setCheck(check == 1);
		String s = JSON.toJSONString(cartItem);
		cartOps.put(skuId.toString(), s);

	}

	@Override
	public void changeItemCount(Long skuId, Integer num) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOperations();
		CartItem cartItem = getCartItem(skuId);
		cartItem.setCount(num);
		String s = JSON.toJSONString(cartItem);
		cartOps.put(skuId.toString(), s);
	}

	@Override
	public void deleteItem(Long skuId) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOperations();
		cartOps.delete(skuId.toString());
	}

	@Override
	public List<CartItem> getUserCartItems() {
		UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
		if (userInfoTo.getUserId() == null) {
			return null;
		} else {
			List<CartItem> cartItems = getCartItems(CART_PREFIX + userInfoTo.getUserId());
			if (cartItems != null && cartItems.size() > 0) {

				return cartItems.stream()
						.filter(CartItem::getCheck)
						//更新为最新价格
						.peek(item -> {
							R r = productFeignService.getPrice(item.getSkuId());
							String data = (String) r.get("data");
							item.setPrice(new BigDecimal(data));
						})
						.collect(Collectors.toList());
			}
		}
		return null;
	}

	//获取到要操作的的购物车
	private BoundHashOperations<String, Object, Object> getCartOperations() {
		UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
		String cartKey = "";
		if (userInfoTo.getUserId() != null ) {
			cartKey = CART_PREFIX + userInfoTo.getUserId();
		} else {
			cartKey = CART_PREFIX + userInfoTo.getUserKey();
		}

		BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
		return operations;
	}

	private List<CartItem> getCartItems(String cartKey) {
		BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(cartKey);
		List<Object> values = hashOps.values();
		if (values != null && values.size() > 0) {
			List<CartItem> collect = values.stream().map(obj -> {
				String str = (String) obj;
				CartItem cartItem = JSON.parseObject(str, CartItem.class);
				return cartItem;
			}).collect(Collectors.toList());
			return collect;
		}
		return null;
	}


}
