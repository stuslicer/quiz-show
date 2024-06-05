package org.example.quizshow.service;

import org.example.quizshow.model.Quiz;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {

    List<Quiz> findAll();
    Optional<Quiz> loadById(String id);
    Quiz saveQuiz(Quiz quiz);
    Quiz updateQuiz(Quiz quiz);
    boolean deleteQuiz(String id);

}
