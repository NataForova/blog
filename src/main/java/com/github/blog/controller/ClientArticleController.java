package com.github.blog.controller;

import com.github.blog.model.Article;
import com.github.blog.model.ArticleInfo;
import com.github.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/articles")
@Tag(name = "Guest article Controller", description = "API for getting articles in guest mode")
public class ClientArticleController {
    private final ArticleService articleService;

    public ClientArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ArticleInfo>> getAllArticles(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        var articlePage = articleService.findAllArticles(pageable);
        return ResponseEntity.ok(articlePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        var article = articleService.findArticleById(id);
        return ResponseEntity.ok(article);
    }
}
