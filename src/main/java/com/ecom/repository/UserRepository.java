package com.ecom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.entity.UserDetail;

public interface UserRepository extends JpaRepository<UserDetail, Long>{
	
	public Boolean existsByMobileNumber(String mobileNumber);
	public Boolean existsByMail(String mail);
	Optional<UserDetail> findByMail(String mail);
	Optional<UserDetail> findByMailAndRole(String mail, String role);
	Optional<UserDetail> findByMobileNumber(String mobileNumber);
	List<UserDetail> findAllUserByRole(String role);
	Optional<UserDetail> findByResetToken(String token);
	

}
