package org.example.quizshow.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizResults {

    private String quizId;
    private int totalQuestions;
    private int correctAnswers;
    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime;

}
