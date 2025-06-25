package com.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.payloads.APPConstants;
import com.ecom.payloads.ApiResponse;
import com.ecom.payloads.CommonUtil;
import com.ecom.payloads.OrderDTO;
import com.ecom.payloads.OrderRequest;
import com.ecom.payloads.OrderResponse;
import com.ecom.services.OrderService;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/user")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	
	@Autowired
	private OrderResponse orderResponse;
	
	
	
	@PostMapping("/order/{userId}")
	public ResponseEntity<ApiResponse> saveOrder(@RequestBody OrderRequest orderRequest, @PathVariable("userId") Long user_Id){
		orderService.saveOrder(user_Id, orderRequest);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Ordered Successfully", true), HttpStatus.CREATED);
	}
	
	@GetMapping("/allorder/{userId}")
	public ResponseEntity<OrderResponse> getAllOrderForUser(@PathVariable("userId") Long user_Id,
			@RequestParam(value = "pageNum", defaultValue = APPConstants.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = APPConstants.PAGE_SIZE) Integer pageSize){
		
		Page<OrderDTO> orderDTOs = orderService.getAllOrderForUser(user_Id, pageNumber, pageSize);
		orderResponse.setContent(orderDTOs.getContent());
		orderResponse.setLastPage(orderDTOs.isLast());
		orderResponse.setPageNumber(orderDTOs.getNumber());
		orderResponse.setPageSize(orderDTOs.getSize());
		orderResponse.setTotalElements(orderDTOs.getTotalElements());
		orderResponse.setTotalPages(orderDTOs.getTotalPages());
		
		return ResponseEntity.ok(orderResponse);
	}
	
	@GetMapping("/order/update_status")
	public ResponseEntity<OrderDTO> updateOrderStatus(@RequestParam("ID") Long Id , @RequestParam("STATUS") Integer status){
		OrderDTO orderDTO=  orderService.updateOrderStatus(Id, status);
		return ResponseEntity.ok(orderDTO);
	}
	
	
	
	
	
	
	
	
}
