package org.example.quizshow.generator;

import org.example.quizshow.generator.openai.model.AiModels;
import org.example.quizshow.model.QuizDifficulty;

/**
 * Represents the configuration for generating a quiz.
 */
public record QuizGeneratorConfig(
        String prompt,
        int numberOfQuestions,
        QuizDifficulty difficulty,
        AiModels aiModels
) {

    public QuizGeneratorConfig(String prompt, int numberOfQuestions) {
        this(prompt, numberOfQuestions, QuizDifficulty.medium, AiModels.GPT_4_TURBO);
    }
    public QuizGeneratorConfig(String prompt) {
        this(prompt, 10, QuizDifficulty.medium, AiModels.GPT_4_TURBO);
    }
}
