package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
	
	public Boolean existsByCategoryName(String categoryName);
	public Category findByCategoryName(String categoryName);
	public List<Category> findByIsActiveTrue();

}
