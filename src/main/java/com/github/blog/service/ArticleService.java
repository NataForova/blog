package com.github.blog.service;

import com.github.blog.exception.ArticleAccessDeniedException;
import com.github.blog.exception.ResourceNotFoundException;
import com.github.blog.model.Article;
import com.github.blog.model.ArticleInfo;
import com.github.blog.model.CreateArticleRequest;
import com.github.blog.model.event.ChangeType;
import com.github.blog.repository.ArticleRepository;
import com.github.blog.repository.UserRepository;
import io.jsonwebtoken.lang.Assert;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ArticleService {
    private final static int MAX_TITLE_LENGTH = 200;
    private final ArticleEventService articleEventService;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;


    public ArticleService(ArticleEventService articleEventService,
                          ArticleRepository articleRepository,
                          UserRepository userRepository) {
        this.articleEventService = articleEventService;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public Page<ArticleInfo> findAllArticles(Pageable pageable) {
        return articleRepository.findAllInfo(pageable);
    }

    @Cacheable(value = "pagedArticles", key = "'page_' + #pageable.pageNumber + '_size_' + #pageable.pageSize")
    public Page<ArticleInfo> findAllAuthorsArticles(String authorEmail, Pageable pageable) {
        return articleRepository.findByAuthor_Email(authorEmail, pageable);
    }

    @Cacheable(value = "articles", key = "#id")
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

    @Transactional
    @CacheEvict(value = {"articles", "pagedArticles"}, allEntries = true)
    public Article createNewArticle(String authorEmail, CreateArticleRequest request) {
        validateArticle(request);
        var optionalAuthor = userRepository.findByEmail(authorEmail);
        if (optionalAuthor.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        var author = optionalAuthor.get();
        var article = new Article()
                .setTitle(request.getTitle())
                .setContent(request.getContent())
                .setAuthor(author)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setIsDeleted(false);

        var savedArticle = articleRepository.save(article);

        articleEventService.saveArticleEvent(savedArticle.getId(),
                request,
                author,
                ChangeType.CREATED);

        return savedArticle;
    }

    @Transactional
    @CacheEvict(value = {"articles", "pagedArticles"}, allEntries = true)
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

        if (!article.getAuthor().getEmail().equals(authorEmail)) {
            throw new ArticleAccessDeniedException("User don't have access to article");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            article.setTitle(request.getTitle());
        }
        if (request.getContent() != null && !request.getContent().isBlank()) {
            article.setContent(request.getContent());
        }
        article.setUpdatedAt(LocalDateTime.now());

        var updated = articleRepository.save(article);

        articleEventService.saveArticleEvent(articleId,
                request,
                optionalAuthor.get(),
                ChangeType.UPDATED);

        return updated;
    }

    @Transactional
    @CacheEvict(value = {"articles", "pagedArticles"}, allEntries = true)
    public void deleteArticleById(Long articleId) {
        var optionalArticle = articleRepository.findById(articleId);
        if (optionalArticle.isEmpty()) {
            throw new ResourceNotFoundException("Article not found");
        }
        var article = optionalArticle.get();
        article.setIsDeleted(true);
        articleRepository.save(article);
        articleEventService.saveArticleEvent(articleId,
                null,
                article.getAuthor(),
                ChangeType.DELETED);
    }

    private void validateArticle(CreateArticleRequest request) {
        Assert.hasText(request.getTitle(), "Title is required");
        Assert.isTrue(request.getTitle().length() <= MAX_TITLE_LENGTH, "Max title length is " + MAX_TITLE_LENGTH);
        Assert.hasText( request.getContent(), "Content is required");
    }

}
