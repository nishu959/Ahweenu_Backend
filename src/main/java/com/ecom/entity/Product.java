package com.ecom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long product_id;
	
	@Column(length=500)
	private String title;
	
	@Column(length = 5000)
	private String description;
	
	private String categoryName;
	private double price;
	private int stock;
	
	private String ImageName;
	
	private Integer discount;
	
	private double discountPrice;
	private Boolean isActive;
	
}
