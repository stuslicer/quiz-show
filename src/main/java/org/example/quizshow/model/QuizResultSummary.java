package org.example.quizshow.model;

import java.time.LocalDateTime;

public record QuizResultSummary(String quizId, int count, int perfect, double averageSuccessRate, double averageTime, LocalDateTime lastPlayed) {
}
