package org.example.quizshow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Duration;
import java.time.LocalDateTime;

public record QuizResult(String quizId, int totalQuestions, int correctAnswers, LocalDateTime startTime,
                         LocalDateTime endTime) {

    @JsonIgnore
    public long getDuration() {
        return Duration.between(startTime(), endTime()).toSeconds();
    }

    @JsonIgnore
    public long getSuccessPercentage() {
        return correctAnswers * 100 / totalQuestions;
    }

    @JsonIgnore
    public boolean isPerfect() {
        return correctAnswers == totalQuestions;
    }

}