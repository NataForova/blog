package com.github.blog.repository;

import com.github.blog.model.Article;
import com.github.blog.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
