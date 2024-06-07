package org.example.quizshow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Log4j2
@Component
public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public JsonUtils(ObjectMapper objectMapper) {
        JsonUtils.objectMapper = objectMapper;
    }

    public static <T> Optional<T> convertJsonToObject(
            File jsonFile,
            Class<T> clazz) {
        return convertJsonToObject(jsonFile, clazz, objectMapper);
    }

    public static <T> Optional<T> convertJsonToObject(
            File jsonFile,
            Class<T> clazz,
            ObjectMapper objectMapper) {
        try {
            T loadedQuiz = objectMapper.readValue(jsonFile, clazz);
            return Optional.of(loadedQuiz);
        } catch (IOException e) {
            log.error("Failed to load quiz from file: {}", jsonFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> convertJsonToObject(
            String jsonString,
            Class<T> clazz) {
        return convertJsonToObject(jsonString, clazz, objectMapper);
    }

    public static <T> Optional<T> convertJsonToObject(
            String jsonString,
            Class<T> clazz,
            ObjectMapper objectMapper) {
        try {
            T loadedQuiz = objectMapper.readValue(jsonString, clazz);
            return Optional.of(loadedQuiz);
        } catch (IOException e) {
            log.error("Failed to load quiz from String: {}", jsonString, e);
            return Optional.empty();
        }
    }

}
