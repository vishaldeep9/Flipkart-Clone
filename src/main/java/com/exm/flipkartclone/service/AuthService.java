package com.exm.flipkartclone.service;


import org.springframework.http.ResponseEntity;

import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.responcedto.UserResponceDto;
import com.exm.flipkartclone.util.ResponceStructure;

public interface AuthService {


	ResponseEntity<ResponceStructure<UserResponceDto>> registerUser( UserRequestDto userRequest);

}
