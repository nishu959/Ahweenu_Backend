package com.ecom.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.payloads.ProductDTO;

public interface ProductService {
	
	ProductDTO saveProduct(ProductDTO productDTO);
	Page<ProductDTO> getAllListOfProducts(Integer pageNo, Integer pageSize);
	ProductDTO updateProduct(Long id, ProductDTO productDTO);
	void deleteProduct(Long id);
	ProductDTO getProductbyId(Long id);
	List<ProductDTO> getAllActiveProducts(String categoryName);
	List<ProductDTO> searchProducts(String str);
	List<ProductDTO> searchProductByAdmin(String str);
	Page<ProductDTO> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String categoryName);
	
	

}
