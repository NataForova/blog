package com.github.blog.controller;

import com.github.blog.model.Article;
import com.github.blog.model.ArticleInfo;
import com.github.blog.model.CreateArticleRequest;
import com.github.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;


@RestController
@RequestMapping("/admin/articles")
@Tag(name = "Admin article controller", description = "API for manage articles in admin mode")
@SecurityScheme(
        name = "bearerAuth",
        type = HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class AuthorArticleController {
    private final ArticleService articleService;

    public AuthorArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ArticleInfo>> getAllArticles(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "100") int size,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size);
        var userEmail = userDetails.getUsername();
        var articlePage = articleService.findAllAuthorsArticles(userEmail, pageable);
        return ResponseEntity.ok(articlePage);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Article> getArticle(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long id) {
        var article = articleService.findAuthorsArticleById(id);
        return ResponseEntity.ok(article);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Article> createArticle(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody CreateArticleRequest request) {
        var userEmail = userDetails.getUsername();
        var article = articleService.createNewArticle(userEmail, request);
        return ResponseEntity.ok(article);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Article> updateArticle(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody CreateArticleRequest request,
                                                 @PathVariable Long id) {
        var userEmail = userDetails.getUsername();
        var article = articleService.updateArticle(userEmail, id, request);
        return ResponseEntity.ok(article);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @DeleteMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticleById(id);
        return ResponseEntity.ok().build();
    }
}
