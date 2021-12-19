package com.qjx.qmall.member.exception;

/**
 * Ryan
 * 2021-11-16-10:00
 */
public class PhoneExistException extends RuntimeException {

	public PhoneExistException() {
		super("Phone Number has already been registered, please check or user another phone number!");
	}
}
