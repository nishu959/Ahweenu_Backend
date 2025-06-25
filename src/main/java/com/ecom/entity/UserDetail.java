package com.ecom.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class UserDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;
	private String name;
	private String mobileNumber;
	private String mail;
	private String address;
	private String city;
	private String state;
	private String pinCode;
	private String password;
	private String profileImage;
	private String role;
	@Column(name = "failed_attempts")
	private int failedAttempts = 0;


	private Boolean accountLocked = false;
	private LocalDateTime lockTime;

	public Boolean isAccountLocked() {
		return this.accountLocked;
	}
	
	private String resetToken;
	private LocalDateTime tokenCreationDate;

}
