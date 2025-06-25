package com.ecom.payloads;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
public class ProductResponse {
	
	private List<ProductDTO> content;
	private Integer PageNumber; 
	private Integer PageSize;
	private Long totalElements;
	private Integer totalPages;
	
	private boolean lastPage;

}
