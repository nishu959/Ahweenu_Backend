package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
	List<Product> findByIsActiveTrue();
	List<Product> findByCategoryNameAndIsActiveTrue(String categoryName);
	List<Product> findByTitleContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(String str1, String str2);
	
	
	
	List<Product> findByIsActiveTrueAndTitleContainingIgnoreCaseOrIsActiveTrueAndCategoryNameContainingIgnoreCase(String str1, String str2);
	Page<Product> findByIsActiveTrue(Pageable pageable);
	Page<Product> findByCategoryNameAndIsActiveTrue(Pageable pageable, String categoryName);

	

}
