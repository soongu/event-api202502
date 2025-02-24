package com.study.event.exception;

public class LoginFailException extends RuntimeException {
    public LoginFailException(String message) {
        super(message);
    }
}
