package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.entity.Orders;
import com.ecom.entity.UserDetail;


public interface OrdersRespository extends JpaRepository<Orders, Long> {
	
	Page<Orders> findByUserDetail(Pageable pageable ,UserDetail userDetail);
	List<Orders> findByOrderIdContainingIgnoreCase(String str);
}
