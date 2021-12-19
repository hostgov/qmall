package com.qjx.qmall.cart.service;

import com.qjx.qmall.cart.vo.Cart;
import com.qjx.qmall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Ryan
 * 2021-11-20-11:21
 */
public interface CartService {
	CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

	CartItem getCartItem(Long skuId);


	Cart getCart() throws ExecutionException, InterruptedException;

	//清空购物车
	void clearCart(String cartKey);

	void checkItem(Long skuId, Integer check);

	void changeItemCount(Long skuId, Integer num);

	void deleteItem(Long skuId);

	List<CartItem> getUserCartItems();
}
