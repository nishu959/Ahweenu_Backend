package com.ecom.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.payloads.OrderDTO;
import com.ecom.payloads.OrderRequest;

public interface OrderService {
	
	public void saveOrder(Long userId, OrderRequest orderRequest);
	Page<OrderDTO> getAllOrderForUser(Long userId,Integer pageNumber , Integer pageSize);
	public OrderDTO updateOrderStatus(Long id, Integer status);
	Page<OrderDTO> getAllOrders(Integer pageNumber, Integer pageSize);
	List<OrderDTO> getAllOrderByOrderID(String orderId);

}
