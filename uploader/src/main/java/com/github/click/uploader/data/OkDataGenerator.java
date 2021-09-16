package com.github.click.uploader.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.Date;

public class OkDataGenerator implements ChData {
    private final ObjectMapper objectMapper;

    public OkDataGenerator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public String getJson() {
        try {
            return objectMapper.writeValueAsString(DataClass
                                                           .builder()
                                                           .id(Math.round(Math.random()*100))
                                                           .name("name_" + String.valueOf(Math.round(Math.random()*100)))
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
        private Long id;
        private String name;
        private Long ts;
        private String description;
    }
}
