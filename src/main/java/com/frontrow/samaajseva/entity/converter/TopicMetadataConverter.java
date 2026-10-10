package com.frontrow.samaajseva.entity.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontrow.samaajseva.pojo.TopicMetadata;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter(autoApply = true)
@Slf4j
public class TopicMetadataConverter implements AttributeConverter<TopicMetadata, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TopicMetadata topicMetadata) {
        try {
            return topicMetadata == null ? null: objectMapper.writeValueAsString(topicMetadata);
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding data to json: " + topicMetadata, ex);
            return null;
        }
    }

    @Override
    public TopicMetadata convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null: objectMapper.readValue(dbData, new TypeReference<TopicMetadata>() {});
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding json from database: " + dbData, ex);
            return null;
        }
    }
}
