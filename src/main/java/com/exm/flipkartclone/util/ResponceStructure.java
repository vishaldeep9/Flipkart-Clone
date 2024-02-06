package com.exm.flipkartclone.util;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ResponceStructure<T> {

	private int status;
	private String message;
	private T data;
	
}
