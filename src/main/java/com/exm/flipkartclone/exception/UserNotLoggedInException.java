package com.exm.flipkartclone.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserNotLoggedInException  extends RuntimeException{
	private String message;

}
