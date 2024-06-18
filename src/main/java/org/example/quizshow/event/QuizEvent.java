package org.example.quizshow.event;

import lombok.Data;

@Data
public abstract sealed class QuizEvent permits QuizGeneratedEvent {
    final EventType type;
}

