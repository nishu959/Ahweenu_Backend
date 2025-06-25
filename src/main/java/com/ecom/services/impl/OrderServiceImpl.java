package com.ecom.services.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecom.entity.Cart;
import com.ecom.entity.OrderAddress;
import com.ecom.entity.Orders;
import com.ecom.entity.UserDetail;
import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.payloads.CommonUtil;
import com.ecom.payloads.OrderDTO;
import com.ecom.payloads.OrderRequest;
import com.ecom.payloads.OrderStatus;
import com.ecom.repository.CartRepository;
import com.ecom.repository.OrderAddressRepository;
import com.ecom.repository.OrdersRespository;
import com.ecom.repository.UserRepository;
import com.ecom.services.OrderService;

@Service
public class OrderServiceImpl implements OrderService{
	
	@Autowired
	private OrdersRespository ordersRespository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderAddressRepository orderAddressRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private CommonUtil commonUtil;
	
	
	@Override
	public void saveOrder(Long userId, OrderRequest orderRequest) {
		
		UserDetail userDetail = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("UserDetail", "UserId", userId));
		List<Cart> carts = cartRepository.findByUserDetail(userDetail);
		for(Cart cart : carts) {
			
			Orders order = new Orders();
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());
			order.setProduct(cart.getProduct());
			Double price = cart.getProduct().getDiscountPrice()*cart.getQuantity();
			order.setPrice(price + 0.18*price + 100);
			order.setQuantity(cart.getQuantity());
			order.setUserDetail(cart.getUserDetail());
			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());
			
			OrderAddress address = new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNumber(orderRequest.getMobileNumber());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setPinCode(orderRequest.getPinCode());
			address.setState(orderRequest.getState());
			
			orderAddressRepository.save(address);
			
			order.setOrderAddress(address);
			
			ordersRespository.save(order);
			
	
		}
		
		try {
			commonUtil.sendMailOrderPlaced(orderRequest);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		
	}

	@Override
	public Page <OrderDTO> getAllOrderForUser(Long userId, Integer pageNumber , Integer pageSize) {
		UserDetail userDetail = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<Orders> orders = ordersRespository.findByUserDetail(pageable,userDetail);
		Page<OrderDTO> orderDTOs = orders.map(order -> orderToOrderDTO(order));
		return orderDTOs;
	}
	
	@Override
	public OrderDTO updateOrderStatus(Long id, Integer status) {
		Orders order = ordersRespository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Order", "orderId", id));
		String setStatus = null;
		
		OrderStatus[] values = OrderStatus.values();
		for(OrderStatus orderStatus: values) {
			if(orderStatus.getId().equals(status)) {
				setStatus = orderStatus.getName();
			}
		}
		
		order.setStatus(setStatus);
		Orders savedOrder = ordersRespository.save(order);
		
		return orderToOrderDTO(savedOrder);
		
	}
	
	@Override
	public Page<OrderDTO> getAllOrders(Integer pageNumber, Integer pageSize) {
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<Orders> orders = ordersRespository.findAll(pageable);
		Page<OrderDTO> orderDTOs = orders.map(order -> orderToOrderDTO(order));
		return orderDTOs;
	}
	
	
	@Override
	public List<OrderDTO> getAllOrderByOrderID(String str) {
		List<Orders> orders = ordersRespository.findByOrderIdContainingIgnoreCase(str);
		List<OrderDTO> orderDTOs = orders.stream().map(order -> orderToOrderDTO(order)).collect(Collectors.toList());
		return orderDTOs;
	}

	
	
	private OrderDTO orderToOrderDTO(Orders orders) {
		return this.modelMapper.map(orders, OrderDTO.class);
	}
	
	private Orders orderDTOtoOrder(OrderDTO orderDTO) {
		return this.modelMapper.map(orderDTO, Orders.class);
	}

	

	
	
	
	

}
