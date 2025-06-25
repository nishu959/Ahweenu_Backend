package com.ecom.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.entity.Cart;
import com.ecom.entity.Product;
import com.ecom.entity.UserDetail;

public interface CartRepository extends JpaRepository<Cart, Long>{
	
//	Cart findByProductIdAndUserDetailId(Long product_id, Long userId);
//	Cart findByProductIdAndUserDetailId(Product product, UserDetail user);
	Cart findByProductAndUserDetail(Product product, UserDetail user);
	List<Cart> findByUserDetail(UserDetail user);
	Long countByUserDetail(UserDetail user);
	
	
//	List<Cart> findByUserDetailId(Long user);
}
