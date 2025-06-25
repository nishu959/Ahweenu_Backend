package com.ecom.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.payloads.ApiResponse;
import com.ecom.payloads.CartDTO;
import com.ecom.services.CartService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private CartService cartService;
	
	@PostMapping("/add")
	public ResponseEntity<CartDTO> addToCart(@RequestParam("userId") Long userId, @RequestParam("productId") Long productId){
		CartDTO cartDTO = cartService.addToCart(userId, productId);
		return ResponseEntity.ok(cartDTO);
		
	}
	
	@GetMapping("/count/{Id}")
	public ResponseEntity<Long> totalCartElement(@PathVariable("Id") Long userId){
		Long count = cartService.getCountCartItems(userId);
		return ResponseEntity.ok(count);
	}
	
	@GetMapping("/products")
	public ResponseEntity<Map<String, Object>> allCarTfromUser(@RequestParam("userId") Long userId){
		List<CartDTO> catCartDTOs = cartService.getCartByUser(userId);
		Double totalOrderValue = catCartDTOs.get(catCartDTOs.size()-1).getTotalAmount();
		Map<String, Object> value = new HashMap<>();
		value.put("cart", catCartDTOs);
		value.put("orderPrice", totalOrderValue);
		return ResponseEntity.ok(value);
	}
	
	@PutMapping("/update-quantity/{cartId}")
	public ResponseEntity<CartDTO> updateQuantity(@PathVariable("cartId") Long cartId, @RequestBody Integer quantity){
		CartDTO cartDTO = cartService.updateQuantity(cartId, quantity);
		return ResponseEntity.ok(cartDTO);
	}
	
	
	@DeleteMapping("/remove/{cartId}")
	public ResponseEntity<ApiResponse> removeItem(@PathVariable("cartId") Long cart_Id){
		cartService.deleteItem(cart_Id);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Cart Item Rmeoved",true), HttpStatus.OK);
	}
	
	

}
