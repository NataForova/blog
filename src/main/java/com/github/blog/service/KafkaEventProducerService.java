package com.github.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class KafkaEventProducerService {

    @Value("${kafka.event.topic.name:default}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    KafkaEventProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(topic, message);
        log.info("Message {} sent to {}", message, topic);
    }

}