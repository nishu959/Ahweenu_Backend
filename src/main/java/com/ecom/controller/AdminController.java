package com.ecom.controller;

import java.io.File;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.entity.Orders;
import com.ecom.payloads.APPConstants;
import com.ecom.payloads.ApiResponse;
import com.ecom.payloads.BucketType;
import com.ecom.payloads.CategoryDTO;
import com.ecom.payloads.CommonUtil;
import com.ecom.payloads.OrderDTO;
import com.ecom.payloads.OrderResponse;
import com.ecom.payloads.ProductDTO;
import com.ecom.payloads.ProductResponse;
import com.ecom.payloads.UserDetailDTO;
import com.ecom.services.CategoryService;
import com.ecom.services.FileService;
import com.ecom.services.OrderService;
import com.ecom.services.ProductService;
import com.ecom.services.UserService;


import jakarta.mail.MessagingException;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ProductResponse productResponse;
	
	@Autowired
	private OrderResponse orderResponse;
	
	@Autowired
	private FileService fileService;
	
	@GetMapping("/")
	public ResponseEntity<String> admin(){
		return ResponseEntity.ok("Admin Page Login");
		
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<?> logIn(@RequestBody Map<String, String> loginData) {
		
	    String username = loginData.get("username");
	    String password = loginData.get("password");
	    
	    try {
	        Map<String, Object> loginResponse = userService.processLoginAdmin(username, password);
	        return ResponseEntity.ok(loginResponse);
	    } catch (LockedException e) {
	        return ResponseEntity.status(HttpStatus.LOCKED).body(Map.of("message", e.getMessage()));
	    } catch (BadCredentialsException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Login error"));
	    }
	}
	
	@GetMapping("/products")
	public ResponseEntity<ProductResponse> getAllProducts(
			@RequestParam(value = "pageNo", defaultValue = APPConstants.PAGE_NUMBER, required = false) Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = APPConstants.PAGE_SIZE, required = false) Integer pageSize
			){
		
		Page<ProductDTO> productDTOs = productService.getAllListOfProducts(pageNo, pageSize);
		productResponse.setContent(productDTOs.getContent());
		productResponse.setLastPage(productDTOs.isLast());
		productResponse.setPageNumber(productDTOs.getNumber());
		productResponse.setPageSize(productDTOs.getSize());
		productResponse.setTotalElements(productDTOs.getTotalElements());
		productResponse.setTotalPages(productDTOs.getTotalPages());
		
		
		
		return ResponseEntity.ok(productResponse);
		
	}
	
	@DeleteMapping("/delete_product/{product_id}")
	public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("product_id") Long id){
		productService.deleteProduct(id);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Product Deleted Successfully", true), HttpStatus.OK);
	}
	
	@PutMapping("/update_product/{product_id}")
	public ResponseEntity<ApiResponse> updateProduct(@PathVariable("product_id") Long id, @RequestPart(value ="product", required = false) ProductDTO productDTO, @RequestPart(value="productImage", required = false) MultipartFile image){
		
		try {
			
			
			String fullUrl;
			if(image!=null) {
				
				fileService.uploadFileS3(image, 2);
				fullUrl =commonUtil.getImageUrl(image, BucketType.PRODUCT.getId());
			} else {
				
				fullUrl =productDTO.getImageName();
			}
			productDTO.setImageName(fullUrl);
			productService.updateProduct(id, productDTO);
			return new ResponseEntity<ApiResponse>(new ApiResponse("Category Updated Successfully", true), HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("Image Upload failed"+ e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/product/{product_id}")
	public ResponseEntity<ProductDTO> getProduct(@PathVariable("product_id") Long id){
		ProductDTO productDTO = productService.getProductbyId(id);
		return ResponseEntity.ok(productDTO);
	}
	
	@PostMapping(value = "/add_product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse> addProduct(@RequestPart("product") ProductDTO productDTO, @RequestPart(value = "imageFile", required = false) MultipartFile imageFile){
		
		try {
			
			String imageName = imageFile!=null ? imageFile.getOriginalFilename(): "default.png";
			String fullUrl =commonUtil.getImageUrl(imageFile, BucketType.PRODUCT.getId());
			if (!imageName.equals("default.png")) {
//				String uploadPath = new File("src/main/resources/static/uploads").getAbsolutePath();
//				File destination = new File(uploadPath + File.separator+ imageName);
//				imageFile.transferTo(destination);
				fileService.uploadFileS3(imageFile, 2);
				
				
			}
			productDTO.setImageName(fullUrl);
			productService.saveProduct(productDTO);
			return new ResponseEntity<ApiResponse>(new ApiResponse("Product Added Successfully", true), HttpStatus.CREATED);
			
		} catch(Exception ex){
			return new ResponseEntity<ApiResponse>(new ApiResponse("Product Addition failed", false), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
//	@PostMapping(value = "/add_category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<ApiResponse> addCategory(@RequestPart("category") CategoryDTO categoryDTO, @RequestPart(value = "image", required = false) MultipartFile imageFile){		
//		
//		
//		System.out.println(imageFile);
//		 try {
//		       
//			 	String imageName = imageFile!=null ? imageFile.getOriginalFilename(): "default.png";
//			 	System.out.println(imageName);
//			 	
//			 	if(!imageName.equals("default.png")) {
//			 		
//			        String uploadPath = new File("src/main/resources/static/uploads").getAbsolutePath();
//			        File dest = new File(uploadPath + File.separator + imageName);
//			        imageFile.transferTo(dest);
//			 	}
//		        
//		        categoryDTO.setImageName(imageName);
//		        categoryService.createCategory(categoryDTO);
//		        return new ResponseEntity<>(new ApiResponse("Category added successfully", true), HttpStatus.CREATED);
//		        
//		    } catch (IOException e) {
//		        return new ResponseEntity<>(new ApiResponse("Image upload failed: " + e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
//		    }
//	}
	
	
	@DeleteMapping("/category/{Id}") 
	public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("Id") Long categoryId) {
		categoryService.deleteCategory(categoryId);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Category Deleted Successfully", true), HttpStatus.OK);
	}
	
	
	@GetMapping("/categories")
	public ResponseEntity<List<CategoryDTO>> getAllCategories(){
		List<CategoryDTO> categoryDTOs = categoryService.getAllCategories();
		return ResponseEntity.ok(categoryDTOs);
	}
	
	@GetMapping("/get_category/{Id}")
	public ResponseEntity<CategoryDTO> getCategory(@PathVariable("Id") Long categoryId){
		CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);
		return ResponseEntity.ok(categoryDTO);
	}
	
	
	@PutMapping("/category/update/{Id}")
	public ResponseEntity<ApiResponse> updateCategory(@PathVariable("Id") Long categoryId, @RequestPart("category") CategoryDTO categoryDTO, @RequestPart(value = "image", required = false) MultipartFile imageFile){
		

		 try {
		       
			 String imageName;
			 String fullUrl;

			 if (imageFile != null) {
			     imageName = imageFile.getOriginalFilename();
			     
//			     String uploadPath = new File("src/main/resources/static/uploads").getAbsolutePath();
//			     File dest = new File(uploadPath + File.separator + imageName);
//			     imageFile.transferTo(dest);
			     
			     fileService.uploadFileS3(imageFile, 1);
			     fullUrl =commonUtil.getImageUrl(imageFile, BucketType.CATEGORY.getId());
			 } else {
			     
				 fullUrl = categoryDTO.getImageName(); // this keeps the original image
			 }
		        
			 	
		        categoryDTO.setImageName(fullUrl);
		        System.out.println(categoryDTO);
		        categoryService.updateCategory(categoryId,categoryDTO);
		        return new ResponseEntity<>(new ApiResponse("Category Updated successfully", true), HttpStatus.CREATED);
		        
		    } catch (Exception e) {
		        return new ResponseEntity<>(new ApiResponse("Image upload failed: " + e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
		    }
		
		
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<UserDetailDTO>> getAllUserByRole(@RequestParam(value = "role") String role){
		List<UserDetailDTO> userDetailDTOs =  userService.getAllUserByRole(role);
		return ResponseEntity.ok(userDetailDTOs);
	}
	
	
	@DeleteMapping("/delete_user/{user_id}")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable("user_id") Long id){
		userService.deleteUser(id);
		return new ResponseEntity<ApiResponse>(new ApiResponse("User Deleted Successfully", true), HttpStatus.OK);
	}
	
	@GetMapping("/orders")
	public ResponseEntity<OrderResponse> gettAllOrders(
			@RequestParam(value ="pageNo", defaultValue = APPConstants.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(value="pageSize", defaultValue = APPConstants.PAGE_SIZE) Integer pageSize
			){
		Page<OrderDTO> orderDtos = orderService.getAllOrders(pageNumber, pageSize);
		orderResponse.setContent(orderDtos.getContent());
		orderResponse.setLastPage(orderDtos.isLast());
		orderResponse.setPageNumber(orderDtos.getNumber());
		orderResponse.setPageSize(orderDtos.getSize());
		orderResponse.setTotalElements(orderDtos.getTotalElements());
		orderResponse.setTotalPages(orderDtos.getTotalPages());
		
		return ResponseEntity.ok(orderResponse);
	}
	
	@GetMapping("/order/update_status")
	public ResponseEntity<OrderDTO> updateOrderStatus(@RequestParam("ID") Long Id , @RequestParam("STATUS") Integer status){
		OrderDTO orderDTO=  orderService.updateOrderStatus(Id, status);
		return ResponseEntity.ok(orderDTO);
	}
	
	
	@PostMapping("/order/sendmail")
	public ResponseEntity<ApiResponse> sendStatusMail(@RequestBody Orders orders) {
		try {
			commonUtil.sendMailProductOrderStatus(orders);
		} catch (UnsupportedEncodingException ex ) {		
			return new ResponseEntity<ApiResponse>(new ApiResponse("Email not send", false), HttpStatus.BAD_REQUEST);		
		}catch (MessagingException ex) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("Email not send", false), HttpStatus.BAD_REQUEST);	
		}
	
		return new ResponseEntity<ApiResponse>(new ApiResponse("Email Sent Successfully", true), HttpStatus.OK);

	}
	
	@GetMapping("/search")
	public ResponseEntity<List<ProductDTO>> searchProductsforAdmin(@RequestParam("key") String str){
		List<ProductDTO> productDTOs=productService.searchProductByAdmin(str);
		return ResponseEntity.ok(productDTOs);
	}
	
	
	@GetMapping("/search/order")
	public ResponseEntity<List<OrderDTO>> searchOrder(@RequestParam("key") String str){
		List<OrderDTO>  orderDTOs = orderService.getAllOrderByOrderID(str);
		return ResponseEntity.ok(orderDTOs);
	}
	
//	@PostMapping("/add")
//	public ResponseEntity<UserDetailDTO> addnewAdmin(@RequestBody UserDetailDTO userDetailDTO){
//		UserDetailDTO userDetailDTO2 = userService.addAdmin(userDetailDTO);
//		return ResponseEntity.ok(userDetailDTO2);
//	}
//	
	@PostMapping(value = "/add")
	public ResponseEntity<ApiResponse> registerAdmin(@RequestPart("Admin") UserDetailDTO userDetailDTO, @RequestPart(value="image", required=false) MultipartFile imageFile){
		
		try {			
			String imageName = imageFile!= null ? imageFile.getOriginalFilename() : "default.img";
			String fullUrl =commonUtil.getImageUrl(imageFile, BucketType.PROFILE.getId());
			if(!imageName.equals("default.img")) {
				
				fileService.uploadFileS3(imageFile, 3);
				
			}
			userDetailDTO.setProfileImage(fullUrl);
			userService.addAdmin(userDetailDTO);
			return new ResponseEntity<ApiResponse>(new ApiResponse("Admin Added Successfully", true), HttpStatus.CREATED);
			
		} catch(Exception ex) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("Admin Addition failed", false), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PutMapping("/update/{id}")
	public ResponseEntity<UserDetailDTO> updateAdmin(@PathVariable("id") Long userId, @RequestPart("Admin") UserDetailDTO userDetailDTO, @RequestPart(value="image", required=false) MultipartFile imageFile){
		UserDetailDTO userDetailDTO2 = userService.updateUser(userDetailDTO, userId, imageFile);
		return ResponseEntity.ok(userDetailDTO2);
		
	}
	
	
	@PutMapping("/updateuser/{id}")
	public ResponseEntity<UserDetailDTO> updateUserByAdmin(@PathVariable("id") Long userId, @RequestPart("User") UserDetailDTO userDetailDTO, @RequestPart(value="image", required=false) MultipartFile imageFile){
		UserDetailDTO userDetailDTO2 = userService.updateUser(userDetailDTO, userId, imageFile);
		return ResponseEntity.ok(userDetailDTO2);
		
	}
	
	

	@PutMapping("change/password/{id}")
	public ResponseEntity<UserDetailDTO> updatePassword(@PathVariable("id") Long userId, @RequestBody String password){
		
		UserDetailDTO userDetailDTO = userService.changePassword(userId, password);
		System.out.println(userDetailDTO);
		return ResponseEntity.ok(userDetailDTO);
	}
	
	
	
	
	@PostMapping(value = "/add_category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse> addCategory(@RequestPart("category") CategoryDTO categoryDTO, @RequestPart(value = "image", required = false) MultipartFile imageFile){		
		
		System.out.println(imageFile);
		 try {
		       
			 	String imageName = imageFile!=null ? imageFile.getOriginalFilename(): "default.png";
			 	String  fullUrl =commonUtil.getImageUrl(imageFile, BucketType.CATEGORY.getId());

			 	
			 	if(!imageName.equals("default.png")) {
			 		
			 		fileService.uploadFileS3(imageFile, 1);
			 		
			 	}
		        
		        categoryDTO.setImageName(fullUrl);
		        categoryService.createCategory(categoryDTO);
		        return new ResponseEntity<>(new ApiResponse("Category added successfully", true), HttpStatus.CREATED);
		        
		    } catch (Exception e) {
		        return new ResponseEntity<>(new ApiResponse("Image upload failed: " + e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
		    }
	}
	
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<UserDetailDTO> getUserDetail(@PathVariable("userId") Long userId){
		UserDetailDTO userDetailDTO = userService.getSingleUser(userId);
		return ResponseEntity.ok(userDetailDTO);
	}
	
	
	
	

	
}
