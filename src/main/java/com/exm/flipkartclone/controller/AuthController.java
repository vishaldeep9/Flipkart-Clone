package com.exm.flipkartclone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.exm.flipkartclone.requestdto.AuthRequest;
import com.exm.flipkartclone.requestdto.OtpModel;
import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.responcedto.AuthResponce;
import com.exm.flipkartclone.responcedto.UserResponceDto;
import com.exm.flipkartclone.service.AuthService;
import com.exm.flipkartclone.util.ResponceStructure;
import com.exm.flipkartclone.util.SimpleResponceStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping(path = "/users")
	public ResponseEntity<ResponceStructure<UserResponceDto>> registerUser(
			@RequestBody @Valid UserRequestDto userRequest) {

		return authService.registerUser(userRequest);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestBody OtpModel otpModel) {

		return authService.verifyOtp(otpModel);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponceStructure<AuthResponce>> login(@RequestBody AuthRequest authRequest,
			HttpServletResponse response) {
		return authService.login(authRequest, response);
	}

	@PostMapping("/logout")
	public ResponseEntity<SimpleResponceStructure<AuthResponce>> logout(@CookieValue(name = "at", required = false) String accessToken,
			@CookieValue(name = "rt", required = false) String refreshToken, HttpServletRequest request,
			HttpServletResponse response) {
		return authService.logout(accessToken, refreshToken, request, response);
	}
	
	@PostMapping("/revoke-all")
	public  ResponseEntity<SimpleResponceStructure<AuthResponce>> revokeAll(){
		
		return authService.revokeAll();
	}
	
	@PostMapping("/revoke-other")
	public void revokeOther(String accessToken,String refreshToken){
		 authService.revokeOther(accessToken,refreshToken);
		
	}
	
	
	
	
}