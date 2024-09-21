package com.github.blog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "author")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_seq")
    @SequenceGenerator(name = "author_seq", sequenceName = "author_sequence", initialValue = 3, allocationSize = 1)
    private Long id;
    private String name;
    private String email;
    private LocalDateTime registrationDate;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Article> articles;
}
