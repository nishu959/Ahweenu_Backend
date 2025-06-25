package com.ecom.services;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.payloads.UserDetailDTO;

public interface UserService {
	
	UserDetailDTO createUser(UserDetailDTO userDetailDTO);
	void deleteUser(Long id);
	UserDetailDTO updateUser(UserDetailDTO userDetailDTO, Long id, MultipartFile imageFile);
	UserDetailDTO getSingleUser(Long id);
	List<UserDetailDTO> getAllUserByRole(String role);	
	Boolean isMobileNumberAlreadyRegistered(String mobileNumber,String role);
	Boolean isEmailIdAlreadyRegistered(String email, String role);
	UserDetailDTO getUserFromEmail(String mail, String role);
	UserDetailDTO getUserFromMobileNumber(String mobileNumber, String role);
	Map<String, Object> processLogin(String username, String password);
	Map<String, Object> processLoginAdmin(String username, String password);
	void updateUserResetToken(Long id, String token);
	void resetPassword(String token, String newPassword);
	UserDetailDTO changePassword(Long id, String newPassword);
	UserDetailDTO addAdmin(UserDetailDTO userDetailDTO);
	

	
	

}
