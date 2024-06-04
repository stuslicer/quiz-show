package org.example.quizshow.generator.openai.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonQuizExtractorTest {
  
    @Test
    public void testExtractJsonForAiQuiz() {
        JsonQuizExtractor jsonQuizExtractor = new JsonQuizExtractor();

        String response = "line 1\nline 2\n```json\nline 4\nline 5\n```\nline 7";
        String expected = "line 4\nline 5";

        String result = jsonQuizExtractor.extractJsonForAiQuiz(response);
      
        assertEquals(expected, result, "Json extraction from response failed");
    }

    @Test
    public void testExtractJsonForAiQuiz_withNoJsonDelimiter() {
        JsonQuizExtractor jsonQuizExtractor = new JsonQuizExtractor();

        String response = "line 1\nline 2\nline 4\nline 5\nline 7";
        String expected = "";

        String result = jsonQuizExtractor.extractJsonForAiQuiz(response);
      
        assertEquals(expected, result, "Json extraction from response should return empty string");
    }
}