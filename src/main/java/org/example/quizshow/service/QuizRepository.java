package org.example.quizshow.service;

import org.example.quizshow.model.Quiz;

import java.util.Optional;

public interface QuizRepository {

    Optional<Quiz> loadById(String id);
    Quiz saveQuiz(Quiz quiz);
    Quiz updateQuiz(Quiz quiz);
    boolean deleteQuiz(String id);

}
