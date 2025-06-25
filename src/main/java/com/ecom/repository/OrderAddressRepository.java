package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.entity.OrderAddress;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long>{

}
