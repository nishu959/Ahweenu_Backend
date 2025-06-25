package com.ecom.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecom.entity.UserDetail;



public class CustomUserDetails implements UserDetails {
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final UserDetail userDetail;

    public CustomUserDetails(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userDetail.getRole()));
    }

    @Override
    public String getPassword() {
        return userDetail.getPassword();
    }

    @Override
    public String getUsername() {
        return userDetail.getMail();
    }
    



}
