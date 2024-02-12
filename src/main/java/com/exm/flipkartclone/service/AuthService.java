package com.exm.flipkartclone.service;


import org.springframework.http.ResponseEntity;

import com.exm.flipkartclone.requestdto.AuthRequest;
import com.exm.flipkartclone.requestdto.OtpModel;
import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.responcedto.AuthResponce;
import com.exm.flipkartclone.responcedto.UserResponceDto;
import com.exm.flipkartclone.util.ResponceStructure;
import com.exm.flipkartclone.util.SimpleResponceStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {


	ResponseEntity<ResponceStructure<UserResponceDto>> registerUser( UserRequestDto userRequest);

	ResponseEntity<String> verifyOtp(OtpModel otpModel);

	ResponseEntity<ResponceStructure<AuthResponce>> login(AuthRequest authRequest, HttpServletResponse response);

	ResponseEntity<SimpleResponceStructure<AuthResponce>> logout(String accessToken, String refreshToken,
			HttpServletRequest request, HttpServletResponse response);

}
