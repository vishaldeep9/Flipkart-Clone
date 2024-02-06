package com.exm.flipkartclone.serviceimpl;

import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.exm.flipkartclone.entity.Users;
import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.service.AuthService;
import com.exm.flipkartclone.util.ResponceStructure;

@Service
public class AuthServiceImpl implements AuthService{
	
	@SuppressWarnings("unchecked") //<T extends Users>----> DataType of T, T--> Actual Data returning
	public <T extends Users>T mapToUser(UserRequestDto userRequestDto) {
		return (T)Users.builder().email(userRequestDto.getEmail()).password(userRequestDto.getPassword()).userRole(userRequestDto.getUserRole())
				.build();
	}

	
	
	
	@Override
	public ResponseEntity<ResponceStructure<User>> registerUser(UserRequestDto userRequest) {
		
		return null;
	}

	

}
