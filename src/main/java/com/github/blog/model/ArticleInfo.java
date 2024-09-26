package com.github.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ArticleInfo implements Serializable {
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    public ArticleInfo(String title, String content, LocalDateTime createdAt, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }
}
