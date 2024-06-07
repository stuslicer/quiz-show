package org.example.quizshow.model;

import java.time.LocalDateTime;

public record QuizResult(String quizId, int totalQuestions, int correctAnswers, LocalDateTime startTime,
                         LocalDateTime endTime) {

    public QuizResult {
        startTime = LocalDateTime.now();
    }

}