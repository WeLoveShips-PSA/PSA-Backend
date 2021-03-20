package com.example.PSABackend.exceptions;

import javax.naming.AuthenticationException;

public class InvalidEmailException extends AuthenticationException {
    public InvalidEmailException(String msg) {
        super(msg);
    }
}
