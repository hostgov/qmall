package com.qjx.qmall.cart.controller;

import com.qjx.qmall.cart.service.CartService;
import com.qjx.qmall.cart.vo.Cart;
import com.qjx.qmall.cart.vo.CartItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Ryan
 * 2021-11-20-09:28
 */
@Controller
public class CartController {
	@Resource
	CartService cartService;

	@GetMapping("/currentUserCartItems")
	@ResponseBody
	public List<CartItem> getCurrentUserCartItems() {
		return cartService.getUserCartItems();
	}

	@GetMapping("/deleteItem")
	public String countItem(@RequestParam("skuId") Long skuId) {
		cartService.deleteItem(skuId);
		return "redirect:http://cart.qmall.com/cart.html";
	}

	@GetMapping("/countItem")
	public String countItem(@RequestParam("skuId") Long skuId,
	                        @RequestParam("num") Integer num) {
		cartService.changeItemCount(skuId, num);
		return "redirect:http://cart.qmall.com/cart.html";
	}

	@GetMapping("/checkItem")
	public String checkItem(@RequestParam("skuId") Long skuId,
	                        @RequestParam("check") Integer check) {
		cartService.checkItem(skuId, check);
		return "redirect:http://cart.qmall.com/cart.html";

	}

	@GetMapping("/cart.html")
	public String cartListPage(Model model) throws ExecutionException, InterruptedException {
		Cart cart = cartService.getCart();
		model.addAttribute("cart", cart);

		return "cartList";
	}

	//添加商品到购物车
	@GetMapping("/addToCart")
	public String addToCart(@RequestParam("skuId") Long skuId,
	                        @RequestParam("num") Integer num,
	                        RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
//		CartItem cartItem = cartService.addToCart(skuId, num);
//		redirectAttributes.addAttribute("cartItem", cartItem);
		cartService.addToCart(skuId, num);
		redirectAttributes.addAttribute("skuId", skuId);
		return "redirect:http://cart.qmall.com/addToCartSuccess.html";
	}

	@GetMapping("/addToCartSuccess.html")
	public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
		CartItem item = cartService.getCartItem(skuId);
		model.addAttribute("cartItem", item);
		return "success";
	}

}
