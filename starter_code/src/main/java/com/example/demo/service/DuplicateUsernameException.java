package com.example.demo.service;

public class DuplicateUsernameException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicateUsernameException(String message) {
		super(message);
	}
}
