package com.ecom.controller;



import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.payloads.ApiResponse;
import com.ecom.payloads.CommonUtil;
import com.ecom.payloads.ForgotPasswordRequest;
import com.ecom.payloads.UserDetailDTO;
import com.ecom.services.CartService;
import com.ecom.services.UserService;

import jakarta.mail.MessagingException;






@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private CommonUtil commonUtil;
	
//	@PostMapping("/login")
//	public ResponseEntity<?> logIn(@RequestBody Map<String, String> loginData) {
//		 	String username = loginData.get("username");
//	        String password = loginData.get("password");
//
//	        Authentication auth = authenticationManager.authenticate(
//	            new UsernamePasswordAuthenticationToken(username, password)
//	        );
//
//	        if (auth.isAuthenticated()) {
//	            String role = auth.getAuthorities().iterator().next().getAuthority();
//	            return ResponseEntity.ok(Map.of("message", "Login successful", "role", role));
//	        } else {
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//	        }
//	}
	
	
	@PostMapping("/login")
	public ResponseEntity<?> logIn(@RequestBody Map<String, String> loginData) {
		
	    String username = loginData.get("username");
	    String password = loginData.get("password");
	    
	    try {
	        Map<String, Object> loginResponse = userService.processLogin(username, password);
	        return ResponseEntity.ok(loginResponse);
	    } catch (LockedException e) {
	        return ResponseEntity.status(HttpStatus.LOCKED).body(Map.of("message", e.getMessage()));
	    } catch (BadCredentialsException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Login error"));
	    }
	}
//	    try {
//	    	
//	    	UserDetailDTO  userDetailDTO = userService.getNameFromEmail(username);
//	        Authentication auth = authenticationManager.authenticate(
//	            new UsernamePasswordAuthenticationToken(username, password)
//	        );
//
//	        List<String> roles = auth.getAuthorities().stream()
//	                .map(GrantedAuthority::getAuthority)
//	                .collect(Collectors.toList());
//
//	        return ResponseEntity.ok(Map.of(
//	            "message", "Login successful",
//	            "username", username,
//	            "roles", roles,
//	            "detail", userDetailDTO
//	        ));
//	    } catch (AuthenticationException e) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//	            Map.of("message", "Invalid credentials")
//	        );
//	    }
//	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logged out");
	}
	
	
	
	@PostMapping("/forgot")
	public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) throws UnsupportedEncodingException, MessagingException{
		
	try {
		 UserDetailDTO userDetailDTO =userService.getUserFromEmail(request.getEmail(),"ROLE_USER");
		 String resetToken = UUID.randomUUID().toString();
		 userService.updateUserResetToken(userDetailDTO.getUserId(), resetToken);
 
		 String resetLink = request.getRedirectUrl() + "?token=" + resetToken;
		 commonUtil.sendMail(request.getEmail(), resetLink);
		 
		 
	} catch (ResourceNotFoundException e) {
		return new ResponseEntity<ApiResponse>(new ApiResponse("Email not send", false), HttpStatus.BAD_REQUEST);
	}
	 
	  return new ResponseEntity<ApiResponse>(new ApiResponse("Email Sent", true), HttpStatus.OK);
	
	}
	
	
	@PostMapping("/reset")
	public ResponseEntity<ApiResponse> resetPassword(@RequestBody Map<String, String> resetData){
		
		String token = resetData.get("token");
	    String new_password = resetData.get("password");
	  
		userService.resetPassword(token, new_password);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Password Reset SuccessFully", true), HttpStatus.OK);
	}
	
	

	@PutMapping("/update/{id}")
	public ResponseEntity<UserDetailDTO> updateUser(@PathVariable("id") Long userId, @RequestPart("User") UserDetailDTO userDetailDTO, @RequestPart(value="image", required=false) MultipartFile imageFile){
		UserDetailDTO userDetailDTO2 = userService.updateUser(userDetailDTO, userId, imageFile);
		return ResponseEntity.ok(userDetailDTO2);
		
	}
	
	@PutMapping("change/password/{id}")
	public ResponseEntity<UserDetailDTO> updatePassword(@PathVariable("id") Long userId, @RequestBody String password){
		UserDetailDTO userDetailDTO = userService.changePassword(userId, password);
		System.out.println(userDetailDTO);
		return ResponseEntity.ok(userDetailDTO);
	}
	
//	@GetMapping("{userId}")
//	public ResponseEntity<UserDetailDTO> getUserDetail(@PathVariable("userId") Long userId){
//		UserDetailDTO userDetailDTO = userService.getSingleUser(userId);
//		return ResponseEntity.ok(userDetailDTO);
//	}
	
	


}
