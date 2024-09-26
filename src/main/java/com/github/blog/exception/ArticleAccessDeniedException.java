package com.github.blog.exception;

public class ArticleAccessDeniedException extends RuntimeException {
    public ArticleAccessDeniedException(String message) {
        super(message);
    }
}
