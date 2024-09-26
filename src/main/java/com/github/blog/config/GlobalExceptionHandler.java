package com.github.blog.config;


import com.github.blog.exception.InvalidEmailException;
import com.github.blog.exception.PasswordConstraintException;
import com.github.blog.exception.ResourceNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.github.blog.exception.ArticleAccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { BadCredentialsException.class})
    protected ResponseEntity<Object> handleBadCredentials(RuntimeException ex, WebRequest request) {
        String body = "Access is forbidden: " + ex.getMessage();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = { ArticleAccessDeniedException.class, ExpiredJwtException.class, ArticleAccessDeniedException.class})
    protected ResponseEntity<Object> handleForbidden(RuntimeException ex, WebRequest request) {
        String body = "Access is forbidden: " + ex.getMessage();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        String body = "Resource not found: " + ex.getMessage();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {InvalidEmailException.class,
            PasswordConstraintException.class,
            IllegalArgumentException.class})
    protected ResponseEntity<Object> handleBadRequest(ResourceNotFoundException ex, WebRequest request) {
        String body = "Resource not found: " + ex.getMessage();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleInternalServerError(Exception ex, WebRequest request) {
        String body = "An internal server error occurred.";
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}