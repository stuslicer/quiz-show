package org.example.quizshow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.example.quizshow.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@Service
public class FileBasedQuizRepository implements QuizRepository {

    private final String baseDirectory;

    private final ObjectMapper objectMapper;

    @Autowired
    public FileBasedQuizRepository(@Value("${quiz.repository.dir}") String baseDirectory,
                                   ObjectMapper objectMapper) {
        this.baseDirectory = baseDirectory;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Quiz> loadById(String id) {
        Assert.notNull(id, "Quiz id must be set.");
        File file = new File(this.baseDirectory, generateQuizFileName(quiz));
        try {
            Quiz loadedQuiz = objectMapper.readValue(file, Quiz.class);
            return Optional.of(loadedQuiz);
        } catch (IOException e) {
            log.error("Failed to load quiz from file: {}", file.getAbsolutePath(), e);
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Quiz saveQuiz(Quiz quiz) {
        Assert.notNull(quiz, "Quiz must not be null");
        Assert.notNull(quiz.getId(), "Quiz id must be set.");

        try {
            File file = new File(this.baseDirectory, generateQuizFileName(quiz));
            quiz.setLastUpdatedOn(LocalDateTime.now());
            objectMapper.writeValue(file, quiz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return quiz;
    }

    @Override
    public Quiz updateQuiz(Quiz quiz) {
        return saveQuiz(quiz);
    }

    @Override
    public boolean deleteQuiz(String id) {
        Assert.notNull(id, "Quiz id must be set.");

        Optional<Quiz> loadedQuiz = loadById(id);

        if( loadedQuiz.isPresent() ) {
            File file = new File(this.baseDirectory, generateQuizFileName(loadedQuiz.get()));
            return file.delete();
        }
        return false;
    }

    private String generateQuizFileName(Quiz quiz) {
        return "quiz-%s.json".formatted(quiz.getId().toLowerCase());
    }
}
