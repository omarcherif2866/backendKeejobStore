package com.example.keejobstore.converter;

import com.example.keejobstore.entity.CVandLetterSection;
import com.example.keejobstore.entity.EvaluationSection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;
@Converter
public class CVConverter implements AttributeConverter<List<CVandLetterSection>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<CVandLetterSection> sections) {
        try {
            return objectMapper.writeValueAsString(sections);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur conversion Section -> JSON", e);
        }
    }

    @Override
    public List<CVandLetterSection> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<CVandLetterSection>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
