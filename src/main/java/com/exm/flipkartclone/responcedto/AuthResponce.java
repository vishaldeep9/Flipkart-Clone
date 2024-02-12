package com.exm.flipkartclone.responcedto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponce {
	private int userId;
	private String userName;
	private String role;
	private boolean isAuthenticated;
	private LocalDateTime accessExpiration;
	private LocalDateTime refreshExpriration;
}
