package com.github.blog.model.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "article_event")
public class ArticleEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    @SequenceGenerator(name = "event_seq", sequenceName = "event_seq", allocationSize = 1)
    private Long id;
    private Long articleId;
    @Column(name = "user_id")
    private Long authorId;
    private ChangeType changeType;
    @Convert(converter = ChangesAttributeConverter.class)
    @Column(name = "changes")
    private List<Changes> changes;
    private LocalDateTime createdAt;
    @JsonIgnore
    private Boolean isSent;
}
