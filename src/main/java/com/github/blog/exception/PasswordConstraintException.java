package com.github.blog.exception;

public class PasswordConstraintException extends RuntimeException {
    public PasswordConstraintException(String message) {
        super(message);
    }
}
