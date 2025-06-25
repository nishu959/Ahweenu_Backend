package com.ecom.payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDTO {
	
	private Long category_id;
	private String categoryName;
	private String imageName;
	
	private Boolean isActive;
	

}
