package com.github.blog.service;

import com.github.blog.exception.ResourceNotFoundException;
import com.github.blog.model.Article;
import com.github.blog.model.ArticleInfo;
import com.github.blog.model.CreateArticleRequest;
import com.github.blog.repository.ArticleRepository;
import com.github.blog.repository.UserRepository;
import io.jsonwebtoken.lang.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ArticleService {
    private final static int MAX_TITLE_LENGTH = 200;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;


    public ArticleService(ArticleRepository articleRepository,
                          UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public Page<ArticleInfo> findAllArticles(Pageable pageable) {
        return articleRepository.findAllInfo(pageable);
    }

    public Page<ArticleInfo> findAllAuthorsArticles(String authorEmail, Pageable pageable) {
        return articleRepository.findByAuthor_Email(authorEmail, pageable);
    }

    public Article findArticleById(Long id) {
        var optionalArticle = articleRepository.findById(id);
        if (optionalArticle.isEmpty()) {
            throw new ResourceNotFoundException("Article not found");
        }
        var article = optionalArticle.get();
        if (article.getIsDeleted()) {
            throw new ResourceNotFoundException("Article is deleted");
        }
        return article;
    }

    public Article findAuthorsArticleById(Long id) {
        var optionalArticle = articleRepository.findById(id);
        if (optionalArticle.isEmpty()) {
            throw new ResourceNotFoundException("Article not found");
        }
        var article = optionalArticle.get();
        if (article.getIsDeleted()) {
            throw new ResourceNotFoundException("Article is deleted");
        }
        return article;
    }

    @Transactional
    public Article createNewArticle(String authorEmail, CreateArticleRequest request) {
        validateArticle(request);
        var optionalAuthor = userRepository.findByEmail(authorEmail);
        if (optionalAuthor.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        var article = new Article()
                .setTitle(request.getTitle())
                .setContent(request.getContent())
                .setAuthor(optionalAuthor.get())
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setIsDeleted(false);

        return articleRepository.save(article);
    }

    @Transactional
    public Article updateArticle(String authorEmail, Long articleId, CreateArticleRequest request) {
        Assert.notNull(articleId, "Article id must not be null");
        var optionalAuthor = userRepository.findByEmail(authorEmail);
        if (optionalAuthor.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        var articleOptional = articleRepository.findById(articleId);

        if (articleOptional.isEmpty()) {
            throw new ResourceNotFoundException("Article not found");
        }
        var article = articleOptional.get();

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            article.setTitle(request.getTitle());
        }
        if (request.getContent() != null && !request.getContent().isBlank()) {
            article.setContent(request.getContent());
        }
        article.setUpdatedAt(LocalDateTime.now());

        return articleRepository.save(article);
    }

    @Transactional
    public void deleteArticleById(Long articleId) {
        var optionalArticle = articleRepository.findById(articleId);
        if (optionalArticle.isEmpty()) {
            throw new ResourceNotFoundException("Article not found");
        }
        var article = optionalArticle.get();
        article.setIsDeleted(true);
        articleRepository.save(article);
    }

    private void validateArticle(CreateArticleRequest request) {
        Assert.hasText(request.getTitle(), "Title is required");
        Assert.isTrue(request.getTitle().length() <= MAX_TITLE_LENGTH, "Max title length is " + MAX_TITLE_LENGTH);
        Assert.hasText( request.getContent(), "Content is required");
    }

}
