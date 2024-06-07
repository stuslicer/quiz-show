package org.example.quizshow.model;

public record QuizResultSummary(String quizId, int count, int perfect, double averageSuccessRate, double averageTime) {
}
