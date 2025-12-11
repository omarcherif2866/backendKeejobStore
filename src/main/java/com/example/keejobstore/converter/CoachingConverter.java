package com.example.keejobstore.converter;

import com.example.keejobstore.entity.CoachingSection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;
@Converter
public class CoachingConverter implements AttributeConverter<List<CoachingSection>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<CoachingSection> sections) {
        try {
            return objectMapper.writeValueAsString(sections);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur conversion Section -> JSON", e);
        }
    }

    @Override
    public List<CoachingSection> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<CoachingSection>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
