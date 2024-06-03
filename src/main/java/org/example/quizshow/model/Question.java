package org.example.quizshow.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a question within a quiz.
 * Has 2 or more options, one of which musy be correct.
 * @param number
 * @param text
 * @param options
 */
public record Question(
        int number,
        String text,
        List<QuestionOption> options
) {
    public record QuestionOption(
        int sequence,
        String text,
        boolean isCorrect,
        String explanation
    ) { }
}
