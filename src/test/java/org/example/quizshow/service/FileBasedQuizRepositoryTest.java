package org.example.quizshow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.quizshow.model.Quiz;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileBasedQuizRepositoryTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void loadByIdSuccessfully() throws IOException {
        String quizId = "sampleQuiz1";

        // Setup dependencies and expectations
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        String baseDirectory = "path/to/directory";
        File file = new File(baseDirectory, "quiz-" + quizId.toLowerCase() + ".json");
        Quiz expectedQuiz = new Quiz();

        when(objectMapper.readValue(any(File.class), eq(Quiz.class))).thenReturn(expectedQuiz);

        //Instantiate target repository
        FileBasedQuizRepository quizRepository = new FileBasedQuizRepository(baseDirectory, objectMapper);

        // Execution
        Optional<Quiz> actualQuizOptional = quizRepository.loadById(quizId);

        // Verify state and interaction
        assertTrue(actualQuizOptional.isPresent());
        assertEquals(expectedQuiz, actualQuizOptional.get());
    }

    @Test
    void loadByIdWithIOException() throws IOException {
        String quizId = "sampleQuiz2";

        // Setup dependencies and expectations
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        String baseDirectory = "path/to/directory";

        when(objectMapper.readValue(any(File.class), eq(Quiz.class))).thenThrow(new IOException("Failed to load quiz"));

        //Instantiate target repository
        FileBasedQuizRepository quizRepository = new FileBasedQuizRepository(baseDirectory, objectMapper);

        // Execution
        Optional<Quiz> actualQuizOptional = quizRepository.loadById(quizId);

        // Verify state and interaction
        assertTrue(actualQuizOptional.isEmpty());
    }

    @Test
    void loadByIdWithNullId() {
        // Setup dependencies and expectations
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        String baseDirectory = "path/to/directory";

        //Instantiate target repository
        FileBasedQuizRepository quizRepository = new FileBasedQuizRepository(baseDirectory, objectMapper);

        // Execution
        Assertions.assertThrows(IllegalArgumentException.class, () -> quizRepository.loadById(null));
    }
}