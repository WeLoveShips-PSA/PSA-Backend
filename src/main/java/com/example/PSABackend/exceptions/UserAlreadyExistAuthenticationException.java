package com.example.PSABackend.exceptions;

import javax.naming.AuthenticationException;

public class UserAlreadyExistAuthenticationException extends AuthenticationException {
    public UserAlreadyExistAuthenticationException(String msg) {
        super(msg);
    }
}
