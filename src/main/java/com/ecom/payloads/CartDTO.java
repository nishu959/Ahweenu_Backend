package com.ecom.payloads;

import com.ecom.entity.Product;
import com.ecom.entity.UserDetail;


import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CartDTO {
	
	private Long cartId;
	private UserDetail userDetail;	
	private Product product;
	private Integer quantity;
	private Double totalPrice;
	private Double totalAmount;


}
