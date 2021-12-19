package com.qjx.qmall.member.exception;

/**
 * Ryan
 * 2021-11-16-10:00
 */
public class UserNameExistException extends RuntimeException{
	public UserNameExistException() {
		super("Username has already been registered, please use another username!");
	}
}
