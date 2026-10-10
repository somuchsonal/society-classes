package com.frontrow.samaajseva.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Converter(autoApply = true)
@Slf4j
public class LongListConverter implements AttributeConverter<List<Long>, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Long> longList) {
        try {
            return longList == null ? null: objectMapper.writeValueAsString(longList);
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding data to json: " + longList, ex);
            return null;
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null: objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding json from database: " + dbData, ex);
            return null;
        }
    }
}
