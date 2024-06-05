package org.example.quizshow.generator.openai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.quizshow.generator.openai.model.AiQuizRecords;
import org.example.quizshow.model.FileUtils;
import org.example.quizshow.service.ErrorLoggingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class OpenAiQuizGeneratorTest {

    @Mock
    private OpenAiInterface openAiInterface;
    @Mock
    private OpenAiService openAiService;
    @Mock
    private AiQuizTransformer aiQuizTransformer;
    @Mock
    private ErrorLoggingService errorLoggingService;

    private OpenAiQuizGenerator openAiQuizGenerator;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        openAiQuizGenerator = new OpenAiQuizGenerator(openAiInterface, openAiService, aiQuizTransformer, objectMapper, errorLoggingService);
    }

    @Test
    void loadJsonQuiz() {
//        String quizJson = FileUtils.readFileAsString("src/test/resources/quiz-json-001.json");
//
//        AiQuizRecords.AiQuiz aiQuiz = openAiQuizGenerator.convertJsonToQuiz(quizJson);
//        System.out.println(aiQuiz);
    }
}