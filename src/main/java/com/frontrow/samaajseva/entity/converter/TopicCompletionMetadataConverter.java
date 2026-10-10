package com.frontrow.samaajseva.entity.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontrow.samaajseva.pojo.TopicCompletionMetadata;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter(autoApply = true)
@Slf4j
public class TopicCompletionMetadataConverter implements AttributeConverter<TopicCompletionMetadata, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TopicCompletionMetadata topicCompletionMetadata) {
        try {
            return topicCompletionMetadata == null ? null: objectMapper.writeValueAsString(topicCompletionMetadata);
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding data to json: " + topicCompletionMetadata, ex);
            return null;
        }
    }

    @Override
    public TopicCompletionMetadata convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null: objectMapper.readValue(dbData, new TypeReference<TopicCompletionMetadata>() {});
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding json from database: " + dbData, ex);
            return null;
        }
    }
}
