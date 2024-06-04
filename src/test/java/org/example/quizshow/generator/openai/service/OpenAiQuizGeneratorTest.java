package org.example.quizshow.generator.openai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.quizshow.generator.openai.model.AiQuizRecords;
import org.example.quizshow.model.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpenAiQuizGeneratorTest {

    private OpenAiQuizGenerator openAiQuizGenerator;

    @BeforeEach
    void setUp() {
        openAiQuizGenerator = new OpenAiQuizGenerator();
        ObjectMapper objectMapper = new ObjectMapper();
        openAiQuizGenerator.setObjectMapper(objectMapper);
    }

    @Test
    void loadJsonQuiz() {
        String quizJson = FileUtils.readFileAsString("src/test/resources/quiz-json-001.json");

        AiQuizRecords.AiQuiz aiQuiz = openAiQuizGenerator.convertJsonToQuiz(quizJson);
        System.out.println(aiQuiz);
    }
}