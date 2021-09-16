package com.github.click.uploader.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.*;

public class ErrorDataGenerator implements ChData {
    private final ObjectMapper objectMapper;

    public ErrorDataGenerator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getJson() {
        try {
            return objectMapper.writeValueAsString(DataClass
                                                           .builder()
                                                           .id(UUID.randomUUID().toString())
                                                           .name(String.valueOf(Math.random()))
                                                           .description("desc")
                                                           .ts(new Date().getTime())
                                                           .build());
        } catch (JsonProcessingException e) {
            return "error";
        }
    }

    @Builder
    @Data
    static class DataClass {
        private String id;
        private String name;
        private Long ts;
        private String description;
    }
}
