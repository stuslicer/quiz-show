package org.example.quizshow.generator.openai.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

/**
 * Class representing OpenAI records.
 */
public class OpenAIRecords {

    // For chat requests and responses
    public record ChatRequest(
            String model,
            List<Message> messages,
            double temperature) {
    }

    public record Message(Role role, String content) {
    }

    public record ChatResponse(
            String id,
            String object,
            long created,
            String model,
            Usage usage,
            List<Choice> choices
    ) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Usage(
                int promptTokens,
                int completionTokens,
                int totalTokens) {
        }

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Choice(
                Message message,
                String finishReason,
                int index) {
        }
    }

    // For models
    public record ModelList(List<Model> data) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Model(String id, long created, String ownedBy) {

            @Override
            public String toString() {
                return "Model{id='%s', created=%s, ownedBy='%s'}".formatted(
                        id, Instant.ofEpochSecond(created)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime(),
                        ownedBy);
            }
        }
    }

}
