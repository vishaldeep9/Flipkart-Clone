package com.exm.flipkartclone.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
@AllArgsConstructor
public class UniqueConstraintViolationException  extends RuntimeException{

	

	private String message;
	private HttpStatus status;
	private String rootCause;
}
