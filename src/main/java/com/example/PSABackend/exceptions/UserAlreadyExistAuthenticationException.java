package com.example.PSABackend.exceptions;

import javax.naming.AuthenticationException;

public class UserAlreadyExistAuthenticationException extends PSAException {
    public UserAlreadyExistAuthenticationException(String msg) {
        super(msg);
    }
}
