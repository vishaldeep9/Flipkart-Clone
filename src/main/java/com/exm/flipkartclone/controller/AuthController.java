package com.exm.flipkartclone.controller;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.service.AuthService;
import com.exm.flipkartclone.util.ResponceStructure;

import jakarta.validation.Valid;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping(path= "users/register")
	public ResponseEntity<ResponceStructure<User>> registerUser(@RequestBody @Valid UserRequestDto userRequest){
		
		return authService.registerUser(userRequest);
	}

}
