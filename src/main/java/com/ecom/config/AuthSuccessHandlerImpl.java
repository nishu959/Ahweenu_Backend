//package com.ecom.config;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Service
//public class AuthSuccessHandlerImpl implements AuthenticationSuccessHandler{
//
//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//			Authentication authentication) throws IOException, ServletException {
//		
//		 	response.setStatus(HttpServletResponse.SC_OK);
//	        response.setContentType("application/json");
//
//	        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//	        Set<String> roles = AuthorityUtils.authorityListToSet(authorities);
//
//	        Map<String, Object> responseBody = new HashMap<>();
//	        responseBody.put("message", "Login successful");
//	        responseBody.put("username", authentication.getName());
//	        responseBody.put("roles", roles);
//
//	        // Optionally, add a JWT here if you're using one
//
//	        new ObjectMapper().writeValue(response.getWriter(), responseBody);
//	}
//
//}
