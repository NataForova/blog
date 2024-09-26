package com.github.blog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.blog.model.CreateArticleRequest;
import com.github.blog.model.User;
import com.github.blog.model.event.ArticleEvent;
import com.github.blog.model.event.ChangeType;
import com.github.blog.model.event.Changes;
import com.github.blog.repository.ArticleEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Slf4j
public class ArticleEventService {
    private final static int MAX_EVENTS_PAGE_SIZE = 50;
    private final ArticleEventRepository articleEventRepository;

    private final KafkaEventProducerService kafkaEventProducerService;

    private final ObjectMapper objectMapper;

    public ArticleEventService(ArticleEventRepository articleEventRepository,
                               KafkaEventProducerService kafkaEventProducerService, ObjectMapper objectMapper) {
        this.articleEventRepository = articleEventRepository;
        this.kafkaEventProducerService = kafkaEventProducerService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());    }

    @Scheduled(fixedRate = 10000)
    public void sendEvents() {
        PageRequest pageRequest = PageRequest.of(0, MAX_EVENTS_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        var articlesForSending = articleEventRepository.findAllByIsSent(false,
                pageRequest);
        articlesForSending.forEach(articleEvent -> {
            try {
                var eventMessage = getMessageFromEvent(articleEvent);
                if (eventMessage == null) return;
                kafkaEventProducerService.sendMessage(eventMessage);
                articleEvent.setIsSent(true);
                articleEventRepository.save(articleEvent);
            } catch (Exception e) {
                log.error("Error while sending event {}", articleEvent.getArticleId(), e);
            }
        });
    }

    @Scheduled(cron = "${cron.event.cleanup:-}")
    public void cleanupEvents() {
        var articlesForSending = articleEventRepository.findAllByIsSentAndCreatedAtBefore(true,
                LocalDateTime.now().minusMonths(1));
        articleEventRepository.deleteAll(articlesForSending);
    }

    @Transactional
    public void saveArticleEvent(Long articleId,
                                 CreateArticleRequest request,
                                 User author,
                                 ChangeType changeType) {
        var changesList = new ArrayList<Changes>();
        if (request != null) {
            changesList.add(new Changes("title", request.getTitle()));
            changesList.add(new Changes("content", request.getContent()));

        }
        var newArticleEvent = new ArticleEvent()
                .setArticleId(articleId)
                .setAuthorId(author.getId())
                .setChangeType(changeType)
                .setChanges(changesList)
                .setCreatedAt(LocalDateTime.now())
                .setIsSent(false);
        articleEventRepository.save(newArticleEvent);

    }

    public String getMessageFromEvent(ArticleEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), "Error during serialize object ArticleEvent");
        }
        return null;
    }

}
