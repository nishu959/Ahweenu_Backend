package com.ecom.payloads;

import java.time.LocalDate;
import java.util.Date;

import com.ecom.entity.OrderAddress;
import com.ecom.entity.Product;
import com.ecom.entity.UserDetail;

import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDTO {
	
	private Long id;
	private String orderId;
	private LocalDate OrderDate;
	
	@ManyToOne
	private Product product;
	private Double price;
	private Integer quantity;
	
	@ManyToOne
	private UserDetail userDetail;
	private String status;
	private String paymentType;
	private OrderAddress orderAddress;

}
