package com.ecom.services;

import java.util.List;

import com.ecom.payloads.CartDTO;

public interface CartService {
	
	CartDTO addToCart(Long user_id, Long product_id);
	List<CartDTO> getCartByUser(Long user_Id);
	Long getCountCartItems(Long user_Id);
	CartDTO updateQuantity(Long cart_Id, Integer quantity);
	void deleteItem(Long cart_Id);

}
