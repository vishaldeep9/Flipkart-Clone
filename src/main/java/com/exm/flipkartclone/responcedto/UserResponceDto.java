package com.exm.flipkartclone.responcedto;

import com.exm.flipkartclone.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponceDto {
	private String email;
	private int userId;
	private UserRole userRole;
	private String userName;
	private boolean isEmailVerified;
	private boolean isDeleated;
}
