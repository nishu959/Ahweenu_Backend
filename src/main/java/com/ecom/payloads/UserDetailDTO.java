package com.ecom.payloads;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetailDTO {
	
	private Long userId;
	private String name;
	private String mobileNumber;
	private String mail;
	private String address;
	private String city;
	private String state;
	private String pinCode;
	private String profileImage;
	private String password;
	private String role;
	private int failedAttempts = 0;

	private Boolean accountLocked = false;
	private LocalDateTime lockTime;

	public Boolean isAccountLocked() {
	    return this.accountLocked;
	}
	private String resetToken;
	private LocalDateTime tokenCreationDate;
}
