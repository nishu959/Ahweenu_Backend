package com.ecom.services.impl;

import java.io.File;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.ecom.entity.UserDetail;
import com.ecom.exceptions.AccountLockedBadRequestException;
import com.ecom.exceptions.DataDuplicateException;
import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.payloads.APPConstants;
import com.ecom.payloads.BucketType;
import com.ecom.payloads.CommonUtil;
import com.ecom.payloads.UserDetailDTO;

import com.ecom.repository.UserRepository;
import com.ecom.services.FileService;
import com.ecom.services.UserService;

@Service
public class UserServiceImpl implements UserService{
	

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private CommonUtil commonUtil;
	
	

	@Override
	public UserDetailDTO createUser(UserDetailDTO userDetailDTO) {
		UserDetail userDetail = dtoToUser(userDetailDTO);
		
		System.out.println(userDetail);
		
		if(isEmailIdAlreadyRegistered(userDetailDTO.getMail(),"ROLE_USER")) {
			throw new DataDuplicateException("User", "Mail");
		} if (isMobileNumberAlreadyRegistered(userDetailDTO.getMobileNumber(),"ROLE_USER")) {
			System.out.println("ELSE");
			throw new DataDuplicateException("User", "MobileNumber");
		}
		
		userDetail.setRole("ROLE_USER");
//		userDetail.setFailedAttempts(0);
		String encodePassword = passwordEncoder.encode(userDetailDTO.getPassword());
		userDetail.setPassword(encodePassword);
		UserDetail savedUserDetail= userRepository.save(userDetail);
		System.out.println(savedUserDetail);
		return userToDTO(savedUserDetail);
	}

	@Override
	public void deleteUser(Long id) {
		UserDetail userDetail = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("UserDetail", "UserId", id));
		userRepository.delete(userDetail);
	}

	@Override
	public UserDetailDTO updateUser(UserDetailDTO userDetailDTO, Long id, MultipartFile imageFile) {
		

		UserDetail userDetail = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("UserDetail", "UserId", id));
		try {

			String fullUrl;
			if(imageFile!=null) {
				
				fileService.uploadFileS3(imageFile, 3);
				fullUrl =commonUtil.getImageUrl(imageFile, BucketType.PROFILE.getId());
			} else {
				
				fullUrl = userDetailDTO.getProfileImage();
			}
		  
		        
		   userDetail.setProfileImage(fullUrl);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Image Upload failed!");
			return null;
		} 
		userDetail.setName(userDetailDTO.getName());
		userDetail.setMobileNumber(userDetailDTO.getMobileNumber());
		userDetail.setMail(userDetailDTO.getMail());
		userDetail.setAddress(userDetailDTO.getAddress());
		userDetail.setCity(userDetailDTO.getCity());
		userDetail.setPinCode(userDetailDTO.getPinCode());
		userDetail.setState(userDetailDTO.getState());
		if(userDetailDTO.getAccountLocked()!=null) {
			userDetail.setAccountLocked(userDetailDTO.getAccountLocked());
		}
//		userDetail.setPassword(userDetailDTO.getPassword());
		userRepository.save(userDetail);
		
		UserDetailDTO userDetailDTO2 = userToDTO(userDetail);
//		System.out.println(userDetailDTO2);
	
		return userDetailDTO2;
		
	}

	@Override
	public UserDetailDTO getSingleUser(Long id) {
		UserDetail userDetail = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("UserDetail", "UserId", id));
		return userToDTO(userDetail);
	}

	@Override
	public List<UserDetailDTO> getAllUserByRole(String role) {
		List<UserDetail> userDetails = userRepository.findAllUserByRole(role);
		List<UserDetailDTO> userDetailDTOs = userDetails.stream().map((user) -> userToDTO(user)).collect(Collectors.toList());
		return userDetailDTOs;
	}
	
	@Override
	public Boolean isMobileNumberAlreadyRegistered(String mobileNumber, String role) {
		try {
	        UserDetailDTO userDetailDTO = getUserFromMobileNumber(mobileNumber, role);
	        if (role.equals(userDetailDTO.getRole())) {
	            return true;
	        }
	        return false; 
	    } catch (ResourceNotFoundException ex) {
	        return false;
	    }
	}


	@Override
	public Boolean isEmailIdAlreadyRegistered(String email, String role) {
		try {
	        UserDetailDTO userDetailDTO = getUserFromEmail(email, role);
	        if (role.equals(userDetailDTO.getRole())) {
	            return true;
	        }
	        return false; 
	    } catch (ResourceNotFoundException ex) {
	        return false;
	    }
	}
	
	@Override
	public UserDetailDTO getUserFromEmail(String mail, String role) {
		UserDetail userDetail = userRepository.findByMail(mail).orElseThrow(()-> new ResourceNotFoundException(role, "mail", mail));
		UserDetailDTO userDetailDTO = userToDTO(userDetail);
		return userDetailDTO;
	}
	
	
	
	
	public Map<String, Object> processLogin(String username, String password) {
			
		UserDetail  user = userRepository.findByMailAndRole(username,"ROLE_USER").orElseThrow(() -> new BadCredentialsException("User not found"));
 
	    handleAutoUnlock(user);

	    if (user.isAccountLocked()) {
	        throw new LockedException("Account is locked. Try again later.");
	    }

	    try {
	        Authentication auth = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(username + "|ROLE_USER", password)
	        );
	        
	        resetFailedAttempts(user);
	        
	    } catch (AuthenticationException ex) {
	    	increaseFailedAttempts(user);
	        throw new BadCredentialsException("Invalid credentials");
	    }

	    
	    UserDetailDTO dto = userToDTO(user);
	    dto.setPassword(password);
	    
	    List<String> roles = List.of(user.getRole());

	    return Map.of(
	        "message", "Login successful",
	        "username", username,
	        "roles", roles,
	        "detail", dto,
	        "status", user.isAccountLocked(),
	        "userId", user.getUserId()
	    );
	}

	
	public void increaseFailedAttempts(UserDetail userDetail) {
        int newFailAttempts = userDetail.getFailedAttempts() + 1;
        userDetail.setFailedAttempts(newFailAttempts);

        if (newFailAttempts >= APPConstants.MAX_FAILED_ATTEMPTS) {
        	userDetail.setAccountLocked(true);
            userDetail.setLockTime(LocalDateTime.now());
        }

        userRepository.save(userDetail);
    }

    public void resetFailedAttempts(UserDetail userDetail) {
    	userDetail.setFailedAttempts(0);
        userRepository.save(userDetail);
    }
    
	
    public void handleAutoUnlock(UserDetail user) {
        if (user.isAccountLocked() && user.getLockTime() != null) {
            if (user.getLockTime().plusMinutes(APPConstants.LOCK_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
                user.setAccountLocked(false);
                user.setFailedAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            }
        }
    }
    
    public void updateUserResetToken(Long id, String token) {
    	UserDetail userDetail = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User", "User_Id", id));
    	userDetail.setResetToken(token);
    	userDetail.setTokenCreationDate(LocalDateTime.now());
    	userRepository.save(userDetail);
    }
    
    @Override
	public void resetPassword(String token, String newPassword) {
		UserDetail userDetail = userRepository.findByResetToken(token).orElseThrow(() -> new ResourceNotFoundException("User", "Token", token));
		
		if (userDetail.getTokenCreationDate() == null || 
	        Duration.between(userDetail.getTokenCreationDate(), LocalDateTime.now()).toMinutes() > 30) {
	        throw new RuntimeException("Token has expired. Please request a new one.");
     	}
		
		if(!userDetail.isAccountLocked()) {
			userDetail.setPassword(passwordEncoder.encode(newPassword));
			userDetail.setResetToken(null);
			userRepository.save(userDetail);
			
		} else {
			throw new AccountLockedBadRequestException("Email",userDetail.getMail());
		}
		 
	}
    
    
    @Override
	public UserDetailDTO changePassword(Long id ,String newPassword) {
    	UserDetail userDetail = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User", "userId", id));
    	userDetail.setPassword(passwordEncoder.encode(newPassword));
    	UserDetail savedUser = userRepository.save(userDetail);
		return userToDTO(savedUser);
	}
    
    
    @Override
	public UserDetailDTO addAdmin(UserDetailDTO userDetailDTO) {
    	
    	UserDetail userDetail = dtoToUser(userDetailDTO);
		
		if(isEmailIdAlreadyRegistered(userDetailDTO.getMail(), "ROLE_ADMIN")) {
			throw new DataDuplicateException("ADMIN", "Mail");
		} if (isMobileNumberAlreadyRegistered(userDetailDTO.getMobileNumber(), "ROLE_ADMIN")) {
			throw new DataDuplicateException("ADMIN", "MobileNumber");
		}
		
		userDetail.setRole("ROLE_ADMIN");
		String encodePassword = passwordEncoder.encode(userDetailDTO.getPassword());
		userDetail.setPassword(encodePassword);
		UserDetail savedUserDetail= userRepository.save(userDetail);
		return userToDTO(savedUserDetail);
	}
    
    
    @Override
	public UserDetailDTO getUserFromMobileNumber(String mobileNumber, String role) {
		UserDetail userDetail = userRepository.findByMobileNumber(mobileNumber).orElseThrow(()-> new ResourceNotFoundException(role, mobileNumber, mobileNumber));
		UserDetailDTO userDetailDTO = userToDTO(userDetail);
		return userDetailDTO;
	}
    
    
    public Map<String, Object> processLoginAdmin(String username, String password) {
		
		UserDetail admin = userRepository.findByMailAndRole(username,"ROLE_ADMIN").orElseThrow(() -> new BadCredentialsException("User not found"));
 
	    handleAutoUnlock(admin);

	    if (admin.isAccountLocked()) {
	        throw new LockedException("Account is locked. Try again later.");
	    }

	    try {
	        Authentication auth = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(username+ "|ROLE_ADMIN", password)
	        );
	        
	        resetFailedAttempts(admin);
	        
	    } catch (AuthenticationException ex) {
	    	increaseFailedAttempts(admin);
	        throw new BadCredentialsException("Invalid credentials");
	    }

	    
	    UserDetailDTO dto = userToDTO(admin);
	    dto.setPassword(password);
	    
	    List<String> roles = List.of(admin.getRole());

	    return Map.of(
	        "message", "Login successful",
	        "username", username,
	        "roles", roles,
	        "detail", dto,
	        "status", admin.isAccountLocked(),
	        "adminId", admin.getUserId()
	    );
	}


    
    
    public UserDetailDTO userToDTO(UserDetail userDetail) {
		return modelMapper.map(userDetail, UserDetailDTO.class);
	}
	
	public UserDetail dtoToUser(UserDetailDTO userDetailDTO) {
		return modelMapper.map(userDetailDTO, UserDetail.class);
	}

	
	
	

	

	

	

}
