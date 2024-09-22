package com.github.blog.exception;

public class InvalidEmail extends RuntimeException {
    public InvalidEmail(String message) {
        super(message);
    }
}
