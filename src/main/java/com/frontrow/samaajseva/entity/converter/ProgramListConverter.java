package com.frontrow.samaajseva.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontrow.samaajseva.pojo.Program;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Converter(autoApply = true)
@Slf4j
public class ProgramListConverter implements AttributeConverter<List<Program>, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Program> programList) {
        try {
            return programList == null ? null: objectMapper.writeValueAsString(programList);
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding data to json: " + programList, ex);
            return null;
        }
    }

    @Override
    public List<Program> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null: objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, Program.class));
        } catch (Exception ex) {
            log.error("Unexpected IOEx decoding json from database: " + dbData, ex);
            return null;
        }
    }
}
