package com.github.blog.repository;

import com.github.blog.model.Article;
import com.github.blog.model.ArticleInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAll(Pageable pageable);

    @Query("select new com.github.blog.model.ArticleInfo(a.title, a.content, a.createdAt, a.author.name)" +
            "from Article a where a.isDeleted = false")
    Page<ArticleInfo> findByAuthor_Email(String authorEmail, Pageable pageable);

    @Query("SELECT new com.github.blog.model.ArticleInfo(a.title, a.content, a.createdAt, a.author.name) " +
            "from Article a where a.isDeleted = false")
    Page<ArticleInfo> findAllInfo(Pageable pageable);

    Optional<Article> findByAuthor_EmailAndId(String email, Long id);

}
