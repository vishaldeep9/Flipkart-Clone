package com.exm.flipkartclone.util;

import org.springframework.stereotype.Component;

@Component
public class ResponceStructure<T> {

	private int status;
	private String message;
	private T data;
	public int getStatus() {
		return status;
	}
	public ResponceStructure<T> setStatus(int status) {
		this.status = status;
		return this;
		
	}
	public String getMessage() {
		return message;
	}
	public ResponceStructure<T>setMessage(String message) {
		this.message = message;
		return this;
	}
	public T getData() {
		return data;
	}
	public ResponceStructure<T> setData(T data) {
		this.data = data;
		return this;
	}
	
	
}
