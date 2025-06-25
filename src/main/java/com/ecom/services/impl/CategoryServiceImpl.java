package com.ecom.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.entity.Category;
import com.ecom.exceptions.DataDuplicateException;
import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.payloads.CategoryDTO;
import com.ecom.repository.CategoryRepository;
import com.ecom.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	
	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		
		if (existCategory(categoryDTO.getCategoryName())){
			throw new DataDuplicateException("Category", "Category_Name");
		}
		Category category = dtoToCategory(categoryDTO);
		Category savedCategory =  categoryRepository.save(category);
		CategoryDTO savedCategoryDto = categoryToDTO(savedCategory);
		return savedCategoryDto;
		
	}
	
	

	@Override
	public List<CategoryDTO> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		List<CategoryDTO> categoryDTOs = categories.stream().map( e -> this.categoryToDTO(e)).collect(Collectors.toList());
		return categoryDTOs;
		
	}

	@Override
	public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
		if (existCategory(categoryDTO.getCategoryName()) && id!=findByCategory(categoryDTO.getCategoryName())){
			throw new DataDuplicateException("Category", "Category_Name");
		}
		
		Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "Id", id));
		
//		Category category = new Category();
		category.setCategoryName(categoryDTO.getCategoryName());
		category.setImageName(categoryDTO.getImageName());
		category.setIsActive(categoryDTO.getIsActive());
		
		
		Category savedCategory = categoryRepository.save(category);
		
		CategoryDTO savedCategoryDTO = categoryToDTO(savedCategory);
		return savedCategoryDTO;
		
	}

	@Override
	public void deleteCategory(Long id) {
		Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "Id", id));
		categoryRepository.delete(category);
	}
	
	@Override
	public Boolean existCategory(String categoryName) {
		return categoryRepository.existsByCategoryName(categoryName);
	}
	
	@Override
	public CategoryDTO getCategoryById(Long id) {
		Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "Id", id));
		CategoryDTO categoryDTO = categoryToDTO(category);
		return categoryDTO;
	}
	
	@Override
	public Long findByCategory(String categoryName) {
		Category category = categoryRepository.findByCategoryName(categoryName);
		CategoryDTO categoryDTO = categoryToDTO(category);
		return categoryDTO.getCategory_id();
	}
	
	@Override
	public List<CategoryDTO> getAllActiveCategories() {
		List<Category> categories = categoryRepository.findByIsActiveTrue();
		List<CategoryDTO> activeCategoryDTOs = categories.stream().map(cat -> categoryToDTO(cat)).collect(Collectors.toList());
		return activeCategoryDTOs;
	}


	
	
	
	public CategoryDTO categoryToDTO(Category category) {
		return this.modelMapper.map(category, CategoryDTO.class);
	}
	
	
	public Category dtoToCategory(CategoryDTO categoryDTO) {
		return this.modelMapper.map(categoryDTO, Category.class);
	}



	


	



	


	

}
