package com.frontrow.samaajseva.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontrow.samaajseva.pojo.Review;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Converter(autoApply = true)
@Slf4j
public class ReviewListConverter implements AttributeConverter<List<Review>, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Review> reviewList) {
        try {
            return reviewList == null ? null: objectMapper.writeValueAsString(reviewList);
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding data to json: " + reviewList, ex);
            return null;
        }
    }

    @Override
    public List<Review> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null: objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, Review.class));
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding json from database: " + dbData, ex);
            return null;
        }
    }
}
