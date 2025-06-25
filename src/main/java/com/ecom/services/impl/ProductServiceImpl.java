package com.ecom.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.entity.Product;
import com.ecom.exceptions.InvalidValueException;
import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.payloads.ProductDTO;
import com.ecom.repository.ProductRepository;
import com.ecom.services.ProductService;

@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public Page<ProductDTO> getAllListOfProducts(Integer pageNo, Integer pageSize){
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> products = productRepository.findAll(pageable);
		Page<ProductDTO> productDTOs = products.map(product -> productToDTO(product));
		return productDTOs;
	}

	@Override
	public ProductDTO saveProduct(ProductDTO productDTO) {
		Product product = dtoToProduct(productDTO);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product savedProduct = productRepository.save(product);
		return productToDTO(savedProduct);
		
	}
	
	@Override
	public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
		
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "Id", id));
		
		if(productDTO.getDiscount() < 0 || productDTO.getDiscount()>100) {
			throw new InvalidValueException("Product","Discount",productDTO.getDiscount());
		} else {
		product.setTitle(productDTO.getTitle());
		product.setDescription(productDTO.getDescription());
		product.setImageName(productDTO.getImageName());
		product.setCategoryName(productDTO.getCategoryName());
		product.setStock(productDTO.getStock());
		product.setPrice(productDTO.getPrice());
		product.setDiscount(productDTO.getDiscount());
		product.setIsActive(productDTO.getIsActive());
		
		Double discount = (product.getPrice() * (product.getDiscount()/100.0));
		Double finalPrice = product.getPrice()-discount;

		product.setDiscountPrice(finalPrice);	
		productRepository.save(product);		
		ProductDTO savedProductDto = productToDTO(product);
		return savedProductDto;
		}
		
	}

	@Override
	public void deleteProduct(Long id) {
		Product product=productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "Id", id));
		productRepository.delete(product);
	}
	
	@Override
	public ProductDTO getProductbyId(Long id) {
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "Id", id));
		ProductDTO productDTO = productToDTO(product);
		return productDTO;
	}
	
//	@Override
//	public List<ProductDTO> getAllActiveProducts(String categoryName) {
//		List<Product> products = null;
//		
//		if(categoryName.equals("")) {
//			products = productRepository.findByIsActiveTrue();	
//		}
//		else {
//			products = productRepository.findByCategoryNameAndIsActiveTrue(categoryName);	
//		}
//		List<ProductDTO> productDTOs = products.stream().map((p) -> productToDTO(p)).collect(Collectors.toList());
//		return productDTOs;
//		
//	}
	
	
	@Override
	public List<ProductDTO> searchProducts(String str) {
		List<Product> products = productRepository.findByIsActiveTrueAndTitleContainingIgnoreCaseOrIsActiveTrueAndCategoryNameContainingIgnoreCase(str, str);
		List<ProductDTO> productDTOs = products.stream().map((product)-> productToDTO(product)).collect(Collectors.toList());
		return productDTOs;
	}
	
	
	@Override
	public List<ProductDTO> searchProductByAdmin(String str) {
		List<Product> products = productRepository.findByTitleContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(str, str);
		List<ProductDTO> productDTOs = products.stream().map((product) -> productToDTO(product)).collect(Collectors.toList());
		return productDTOs;
	}

	@Override
	public Page<ProductDTO> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String categoryName) {
		
		Pageable pageable = PageRequest.of(pageNo, pageSize); 
		Page<Product> pageProduct = null;
		
		if(categoryName.equals("")) {
			pageProduct = productRepository.findByIsActiveTrue(pageable);
		
		}
		else {
			pageProduct = productRepository.findByCategoryNameAndIsActiveTrue(pageable ,categoryName);	
		}
		
		Page<ProductDTO> pageProductDTO = pageProduct.map(product -> productToDTO(product));	

		
		return pageProductDTO;
		
	}
	
	
	@Override
	public List<ProductDTO> getAllActiveProducts(String categoryName) {
		List<Product> products;
		if(categoryName.equals("")) {
			products = productRepository.findByIsActiveTrue();
		} else {
			products =  productRepository.findByCategoryNameAndIsActiveTrue(categoryName);
		}
		
		List<ProductDTO> productDTOs = products.stream().map((product) -> productToDTO(product)).collect(Collectors.toList());
		return productDTOs;
	}

	public Product dtoToProduct(ProductDTO productDTO) {
		return modelMapper.map(productDTO, Product.class);
	}
	
	public ProductDTO productToDTO(Product product) {
		return modelMapper.map(product, ProductDTO.class);
	}

	

	
	
	

	
	
	

}
