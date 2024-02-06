package com.exm.flipkartclone.service;

import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;

import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.util.ResponceStructure;

public interface AuthService {


	ResponseEntity<ResponceStructure<User>> registerUser( UserRequestDto userRequest);

}
