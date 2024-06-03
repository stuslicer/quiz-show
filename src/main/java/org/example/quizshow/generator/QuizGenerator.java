package org.example.quizshow.generator;

import org.example.quizshow.model.Quiz;

/**
 * An abstraction for generating a new quiz.
 */
public interface QuizGenerator {

    /**
     * Generates a new quiz based on the provided configuration.
     *
     * @param config the configuration for generating the quiz
     * @return the generated quiz
     */
    Quiz generateQuiz(QuizGeneratorConfig config);

}
