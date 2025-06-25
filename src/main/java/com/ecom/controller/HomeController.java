package com.ecom.controller;



import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.payloads.APPConstants;
import com.ecom.payloads.ApiResponse;
import com.ecom.payloads.BucketType;
import com.ecom.payloads.CategoryDTO;
import com.ecom.payloads.CommonUtil;
import com.ecom.payloads.ProductDTO;
import com.ecom.payloads.UserDetailDTO;
import com.ecom.services.CategoryService;
import com.ecom.services.FileService;
import com.ecom.services.ProductService;
import com.ecom.services.UserService;


@CrossOrigin(origins ="http://localhost:4200")
@RestController
@RequestMapping("/home")
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private CommonUtil commonUtil;
	
	
//	@GetMapping("/")
//	public ResponseEntity<String> home(){
//		return ResponseEntity.ok("Home Page");
//	}
	
//	@GetMapping("/login")
//	public ResponseEntity<String> login(){
//		return ResponseEntity.ok("Login Page");
//	}
	
	@PostMapping(value = "/register")
	public ResponseEntity<ApiResponse> registerUser(@RequestPart("User") UserDetailDTO userDetailDTO, @RequestPart(value="image", required=false) MultipartFile imageFile){
		
		
		try {			
			String imageName = imageFile!= null ? imageFile.getOriginalFilename() : "default.img";
			if(!imageName.equals("default.img")) {
				
				fileService.uploadFileS3(imageFile, 3);
				
			}
			
			String fullUrl =commonUtil.getImageUrl(imageFile, BucketType.PROFILE.getId());
			userDetailDTO.setProfileImage(fullUrl);
			userService.createUser(userDetailDTO);
			return new ResponseEntity<ApiResponse>(new ApiResponse("User Added Successfully", true), HttpStatus.CREATED);
			
		} catch(Exception ex) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("User Addition failed", false), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/user/update/{id}")
	public ResponseEntity<ApiResponse> updateUser(@PathVariable("id") Long user_id, @RequestPart(value = "imageFile") MultipartFile image, @RequestPart(value = "user") UserDetailDTO userDetailDTO){
		return null;
	}
	
	@GetMapping("/products")
	public ResponseEntity<Map<String, Object>> products(@RequestParam(value="category", defaultValue = "") String categoryName,
			@RequestParam(value = "pageNo", defaultValue = APPConstants.PAGE_NUMBER, required = false) Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = APPConstants.PAGE_SIZE, required = false) Integer pageSize){
		
		Page<ProductDTO> pageProductDTOs = productService.getAllActiveProductPagination(pageNo, pageSize,categoryName);
		List<CategoryDTO> categoryDTOs = categoryService.getAllActiveCategories();
		
		Map<String , Object> mapOfObjs = new HashMap<>();
		
		mapOfObjs.put("Products", pageProductDTOs.getContent());
		mapOfObjs.put("Categories", categoryDTOs);
		mapOfObjs.put("PageNo", pageProductDTOs.getNumber());
		mapOfObjs.put("PageSize", pageProductDTOs.getSize());
		mapOfObjs.put("totalProducts", pageProductDTOs.getTotalElements());
		mapOfObjs.put("totalPage", pageProductDTOs.getTotalPages());
		mapOfObjs.put("lastPage", pageProductDTOs.isLast());
		
		return ResponseEntity.ok(mapOfObjs);
	}
	

	@GetMapping("/product/{product_id}")
	public ResponseEntity<ProductDTO> getProduct(@PathVariable("product_id") Long id){
		ProductDTO productDTO = productService.getProductbyId(id);
		return ResponseEntity.ok(productDTO);
	}
	
	@GetMapping("/categories")
	public ResponseEntity<List<CategoryDTO>> getAllActiveCategories(){
		List<CategoryDTO> categoryDTOs = categoryService.getAllActiveCategories();
		return ResponseEntity.ok(categoryDTOs);
	}
	
	@GetMapping("/category/{Id}")
	public ResponseEntity<CategoryDTO> getCategory(@PathVariable("Id") Long categoryId){
		CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);
		return ResponseEntity.ok(categoryDTO);
	}
	
	
	@GetMapping("/search")
	public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam("key") String str){
		List<ProductDTO> productDTOs = productService.searchProducts(str);		
		return ResponseEntity.ok(productDTOs);
	}
	
	@GetMapping("/")
	public ResponseEntity<Map<String , Object>> loginPage(){
		List<CategoryDTO> categoryDTOs = categoryService.getAllActiveCategories().stream().limit(6).collect(Collectors.toList());
		List<ProductDTO> productDTOs = productService.getAllActiveProducts("").stream().limit(8).collect(Collectors.toList());
		
//		categoryDTOs.stream().limit(6);
//		System.out.println(productDTOs.size());
		
		Map<String , Object> mapOfObjs = new HashMap<>();
		mapOfObjs.put("categories", categoryDTOs);
		mapOfObjs.put("products", productDTOs);
		
		return ResponseEntity.ok(mapOfObjs);
	}
	
}
