package com.github.blog.model.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Converter
@Slf4j
public class ChangesAttributeConverter implements AttributeConverter<List<Changes>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Changes> changes) {
        try {
            return objectMapper.writeValueAsString(changes);
        } catch (JsonProcessingException jpe) {
            log.warn("Cannot convert Changes into JSON");
            return "";
        }
    }

    @Override
    public List<Changes> convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, new TypeReference<List<Changes>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert JSON into Address");
            return null;
        }
    }
}
