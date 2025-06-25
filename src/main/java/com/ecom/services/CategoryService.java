package com.ecom.services;

import java.util.List;

import com.ecom.payloads.CategoryDTO;

public interface CategoryService {
	
	CategoryDTO createCategory(CategoryDTO categoryDTO);
	Boolean existCategory(String categoryName);
	List<CategoryDTO> getAllCategories();
	CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
	void deleteCategory(Long id);
	CategoryDTO getCategoryById(Long id);
	Long findByCategory(String categoryName);
	List<CategoryDTO> getAllActiveCategories();

}
