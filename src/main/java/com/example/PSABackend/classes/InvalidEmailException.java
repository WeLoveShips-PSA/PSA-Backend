package com.example.PSABackend.classes;

import javax.naming.AuthenticationException;

public class InvalidEmailException extends AuthenticationException {
    public InvalidEmailException(String msg) {
        super(msg);
    }
}
