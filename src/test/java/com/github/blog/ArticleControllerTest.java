package com.github.blog;

import com.github.blog.controller.ClientArticleController;
import com.github.blog.service.ArticleService;
import com.github.blog.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(ClientArticleController.class)
public class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        // Set up any necessary mock objects or configurations here
    }

    //TODO do this tests

}
