package com.ecom.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.entity.Cart;
import com.ecom.entity.Product;
import com.ecom.entity.UserDetail;
import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.payloads.CartDTO;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.services.CartService;

@Service
public class CartServiceImpl implements CartService{
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public CartDTO addToCart(Long user_id, Long product_id) {
		
		UserDetail userDetail = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User", "Id", user_id));
		Product product = productRepository.findById(product_id).orElseThrow(() -> new ResourceNotFoundException("Product", "Id", product_id));;
		

		Cart cartStatus = cartRepository.findByProductAndUserDetail(product, userDetail);
		Cart  cart= null;		
		
		if(ObjectUtils.isEmpty(cartStatus)) {
			
			cart = new Cart();
			cart.setProduct(product);
			cart.setUserDetail(userDetail);
			cart.setQuantity(1);
			cart.setTotalPrice(1* product.getDiscountPrice());

			
		} else {
			
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity()+1);
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
			
		}
			
		
		Cart savedCart = cartRepository.save(cart);
		System.out.println(savedCart);
		return cartToDTO(savedCart);
		
	}

	@Override
	public List<CartDTO> getCartByUser(Long user_Id) {
		UserDetail userDetail = userRepository.findById(user_Id).orElseThrow(() -> new ResourceNotFoundException("User", "Id", user_Id));
		List<Cart> carts = cartRepository.findByUserDetail(userDetail);
		
		List<CartDTO> cartDTOs = carts.stream().map((cart) -> cartToDTO(cart)).collect(Collectors.toList());
		Double totalOrderPrice = 0.0;
		
		List<CartDTO> updatedCartDTOs = new ArrayList<>();
		for(CartDTO cartDTO : cartDTOs) {
			
			Double totalPrice = (cartDTO.getQuantity() * cartDTO.getProduct().getDiscountPrice());
			cartDTO.setTotalPrice(totalPrice);
			
			totalOrderPrice += totalPrice;
			cartDTO.setTotalAmount(totalOrderPrice);
			updatedCartDTOs.add(cartDTO);
			
		}
		
//		List<CartDTO> cartDTOs = carts.stream().map(cart -> cartToDTO(cart)).collect(Collectors.toList());
		return updatedCartDTOs;
	}
	
	@Override
	public Long getCountCartItems(Long user_Id) {
		UserDetail userDetail = userRepository.findById(user_Id).orElseThrow(() -> new ResourceNotFoundException("User", "Id", user_Id));
		Long count = cartRepository.countByUserDetail(userDetail);
		return count;
	}
	
	
	@Override
	public CartDTO updateQuantity(Long cart_Id, Integer quantity) {
		Cart cart = cartRepository.findById(cart_Id).orElseThrow(() -> new ResourceNotFoundException("Cart", "Cart_ID", cart_Id));
		cart.setQuantity(quantity);
		Cart sevedCart = cartRepository.save(cart);
		return cartToDTO(sevedCart);
	}

	@Override
	public void deleteItem(Long cart_Id) {
		Cart cart = cartRepository.findById(cart_Id).orElseThrow(() -> new ResourceNotFoundException("Cart", "Cart_ID", cart_Id));
		cartRepository.delete(cart);
	}

	
	public Cart DTOtoCart(CartDTO cartDTO) {
		return this.modelMapper.map(cartDTO, Cart.class);
	}
	
	public CartDTO cartToDTO(Cart cart) {
		return this.modelMapper.map(cart, CartDTO.class);
	}

	

	

	
}
