package com.github.blog;

import com.github.blog.exception.ResourceNotFoundException;
import com.github.blog.model.Article;
import com.github.blog.model.CreateArticleRequest;
import com.github.blog.model.User;
import com.github.blog.repository.UserRepository;
import com.github.blog.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
public class ArticleServiceTest {
    private static final String EMAIL = "author@test.com";
    private static final Long TEST_ARTICLE_ID_1 = 1L;
    private static final Long TEST_ARTICLE_ID_2 = 2L;


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createNewArticlePositiveTest() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("Integration Test Title");
        request.setContent("Integration Test Content");
        createTestUser(EMAIL);

        Article article = articleService.createNewArticle(EMAIL, request);

        assertThat(article).isNotNull();
        assertThat(article.getTitle()).isEqualTo("Integration Test Title");
    }

    @Test
    public void createNewArticleWhenEmptyTitleTest() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("");
        request.setContent("Integration Test Content");

        try {
            articleService.createNewArticle(EMAIL, request);
            fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getMessage()).isEqualTo("Title is required");
        }
    }

    @Test
    public void createNewArticleWhenTitleIsNullTest() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle(null);
        request.setContent("Integration Test Content");

        try {
            articleService.createNewArticle(EMAIL, request);
            fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getMessage()).isEqualTo("Title is required");
        }

    }

    @Test
    public void createNewArticleWhenEmptyContentTest() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("Integration Test Title");
        request.setContent("");

        try {
            articleService.createNewArticle(EMAIL, request);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getMessage()).isEqualTo("Content is required");
        }

    }

    @Test
    public void createNewArticleWhenContentIsNullTest() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("Integration Test Title");
        request.setContent(null);

        try {
            articleService.createNewArticle(EMAIL, request);
            fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getMessage()).isEqualTo("Content is required");
        }

    }

    @Test
    public void createNewArticleWhenInvalidEmail() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("Integration Test Title");
        request.setContent("Integration Test Content");

        try {
            articleService.createNewArticle("author1234@test.com", request);
            fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("User not found");

        }
    }

    @Test
    public void updateNewArticleWhenInvalidEmail() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("Updated Test Title");
        request.setContent("Updated Test Content");

        try {
            articleService.updateArticle("author1234@test.com", TEST_ARTICLE_ID_1, request);
            fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("User not found");

        }
    }

    @Test
    public void updateNewArticleWhenTitleChanged() {
        var created = getTestArticle(TEST_ARTICLE_ID_1, "Created Test Title", "Created Test  Article Content");

        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("Updated Test Title");
        request.setContent(null);

        String expectedTitle = "Updated Test Title";
        String expectedContent = created.getContent();

        var actualArticle = articleService.updateArticle(EMAIL, created.getId(), request);

        assertThat(actualArticle).isNotNull();
        assertThat(actualArticle.getTitle()).isEqualTo(expectedTitle);
        assertThat(actualArticle.getContent()).isEqualTo(expectedContent);

    }

    @Test
    public void updateNewArticleWhenContentChanged() {
        var created = getTestArticle(TEST_ARTICLE_ID_2, "Created Test Title", "Created Test  Article Content");

        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle(null);
        request.setContent("Updated Test  Article Content");

        String expectedTitle = created.getTitle();
        String expectedContent = "Updated Test  Article Content";

        var actualArticle = articleService.updateArticle(EMAIL, created.getId(), request);

        assertThat(actualArticle).isNotNull();
        assertThat(actualArticle.getTitle()).isEqualTo(expectedTitle);
        assertThat(actualArticle.getContent()).isEqualTo(expectedContent);

    }

    @Test
    public void deleteArticleWhenWrongIdd() {
        try {
            articleService.deleteArticleById(99L);
            fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Article not found");
        }
    }

    @Test
    public void deleteArticlePositiveTest() {
        var created = getTestArticle(TEST_ARTICLE_ID_2, "Created Test Title", "Created Test  Article Content");
        articleService.deleteArticleById(created.getId());

        try {
            var deleted = articleService.findArticleById(created.getId());
            fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Article is deleted");
        }

    }

    private void createTestUser(String userEmail) {
        var optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            var testUser = new User()
                    .setEmail(userEmail)
                    .setName("test authorov");
            userRepository.save(testUser);
        }
    }

    public Article getTestArticle(Long articleId, String title, String content) {
        createTestUser(EMAIL);
        var createArticleRequest = new CreateArticleRequest()
                .setTitle(title)
                .setContent(content);
        try {
            return articleService.findArticleById(articleId);
        } catch (ResourceNotFoundException e) {
            return articleService.createNewArticle(EMAIL, createArticleRequest);
        }
    }

}
