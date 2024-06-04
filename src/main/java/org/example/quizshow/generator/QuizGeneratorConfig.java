package org.example.quizshow.generator;

import org.example.quizshow.model.QuizDifficulty;

/**
 * Represents the configuration for generating a quiz.
 */
public record QuizGeneratorConfig(
        String prompt,
        int numberOfQuestions,
        QuizDifficulty difficulty
) {

    public QuizGeneratorConfig(String prompt, int numberOfQuestions) {
        this(prompt, numberOfQuestions, QuizDifficulty.medium);
    }
    public QuizGeneratorConfig(String prompt) {
        this(prompt, 10, QuizDifficulty.medium);
    }
}
