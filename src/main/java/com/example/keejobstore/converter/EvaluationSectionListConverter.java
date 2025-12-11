package com.example.keejobstore.converter;

import com.example.keejobstore.entity.EvaluationSection;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

@Converter
public class EvaluationSectionListConverter implements AttributeConverter<List<EvaluationSection>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<EvaluationSection> sections) {
        try {
            return objectMapper.writeValueAsString(sections);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur conversion Section -> JSON", e);
        }
    }

    @Override
    public List<EvaluationSection> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<EvaluationSection>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

