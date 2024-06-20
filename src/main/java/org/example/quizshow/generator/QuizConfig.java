package org.example.quizshow.generator;

import org.example.quizshow.generator.openai.model.AiModels;

import java.util.List;

/**
 * Application wide configurations for the
 * @param defaultAiModel
 */
public record QuizConfig(

        // Default AiModel to use
        AiModels defaultAiModel,

        List<AiModels> aiModelPreferredOrder,

        int pageSize
) {
}
