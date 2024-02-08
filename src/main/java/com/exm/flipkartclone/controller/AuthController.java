package com.exm.flipkartclone.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.exm.flipkartclone.requestdto.OtpModel;
import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.responcedto.UserResponceDto;
import com.exm.flipkartclone.service.AuthService;
import com.exm.flipkartclone.util.ResponceStructure;

import jakarta.validation.Valid;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping(path= "/users")
	public ResponseEntity<ResponceStructure<UserResponceDto>> registerUser(@RequestBody @Valid UserRequestDto userRequest){
		
		return authService.registerUser(userRequest);
	}

	
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestBody OtpModel  otpModel ){
		
		return authService.verifyOtp(otpModel);
	}
}
