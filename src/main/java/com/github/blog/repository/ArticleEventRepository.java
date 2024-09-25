package com.github.blog.repository;

import com.github.blog.model.event.ArticleEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface ArticleEventRepository extends JpaRepository<ArticleEvent, Long> {
    List<ArticleEvent> findAllByIsSent(Boolean isSent, PageRequest pageRequest);
    List<ArticleEvent> findAllByIsSentAndCreatedAtBefore(Boolean isSent, LocalDateTime before);

}
