package com.ecom.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecom.entity.UserDetail;
import com.ecom.payloads.APPConstants;
import com.ecom.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String usernameWithRole) throws UsernameNotFoundException {
		
		String[] parts = usernameWithRole.split("\\|");
		String username = parts[0];
		String role = parts[1];
		    
		UserDetail userDetail = userRepository.findByMailAndRole(username, role)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		if (userDetail.isAccountLocked()) {
			if (userDetail.getLockTime() != null && userDetail.getLockTime()
					.plusMinutes(APPConstants.LOCK_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
				userDetail.setAccountLocked(false);
				userDetail.setFailedAttempts(0);
				userDetail.setLockTime(null);
				userRepository.save(userDetail);
			}
		}

		return new CustomUserDetails(userDetail);
	}

}
