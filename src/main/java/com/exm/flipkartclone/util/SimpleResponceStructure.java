package com.exm.flipkartclone.util;

import org.springframework.stereotype.Component;

@Component
public class SimpleResponceStructure<T> {
private String message;
private int status;
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public int getStatus() {
	return status;
}
public void setStatus(int status) {
	this.status = status;
}



}
