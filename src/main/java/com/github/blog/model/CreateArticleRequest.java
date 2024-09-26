package com.github.blog.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreateArticleRequest {

    @Schema(description = "Title of the article",
            example = "Understanding OpenAPI in Spring Boot",
            defaultValue = "New Article Title")
    private String title;

    @Schema(description = "Content of the article",
            example = "This article explains how to integrate OpenAPI in a Spring Boot application...",
            defaultValue = "Article content goes here...")
    private String content;
}
