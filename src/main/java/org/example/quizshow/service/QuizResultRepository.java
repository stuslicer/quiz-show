package org.example.quizshow.service;

import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizResult;

import java.util.List;
import java.util.Optional;

public interface QuizResultRepository {

    void addResult(QuizResult result);

}
