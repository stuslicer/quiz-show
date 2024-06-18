package org.example.quizshow.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public final class QuizGeneratedEvent extends QuizEvent {

    private final String quizId;
    private final LocalDateTime createdOn;

    public QuizGeneratedEvent(String quizId) {
        super(EventType.QuizGenerated);
        this.quizId = quizId;
        this.createdOn = LocalDateTime.now();
    }
}
