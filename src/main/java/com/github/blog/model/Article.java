package com.github.blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_seq")
    @SequenceGenerator(name = "article_seq", sequenceName = "article_sequence", initialValue = 3, allocationSize = 1)
    private Long id;
    private String title;
    private String content;
    @Column(name = "user_id")
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
