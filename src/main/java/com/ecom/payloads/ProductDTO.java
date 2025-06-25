package com.ecom.payloads;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO {
	private Long product_id;
	private String title;
	private String description;
	private String categoryName;
	private double price;
	private int stock;
	private String imageName;
	private Integer discount;
	
	private double discountPrice;
	private Boolean isActive;

}
