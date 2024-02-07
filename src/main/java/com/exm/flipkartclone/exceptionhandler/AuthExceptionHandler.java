package com.exm.flipkartclone.exceptionhandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.exm.flipkartclone.exception.UniqueConstraintViolationException;

@RestControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> mapErrors = new HashMap<>();
		List<ObjectError> allErrors = ex.getAllErrors();
		for (ObjectError objectError : allErrors) {
			FieldError fieldError = (FieldError) objectError;
			mapErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return exceptionStructure(HttpStatus.BAD_REQUEST, ex.getMessage(), mapErrors);
	}

	private ResponseEntity<Object> exceptionStructure(HttpStatus status, String message, Object rootCause) {

		return new ResponseEntity<Object>(Map.of("Status", status.value(), "Message", message, "Root Cause", rootCause),
				status);
	}
	
	@ExceptionHandler(UniqueConstraintViolationException.class)
	public ResponseEntity<Object> handleContraintException(UniqueConstraintViolationException exception){
		return exceptionStructure(exception.getStatus(),exception.getMessage(),exception.getRootCause());
	}

}
