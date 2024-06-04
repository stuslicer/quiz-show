package org.example.quizshow.generator.openai.service;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JsonQuizExtractor {

    private static final String JSON_START_TOKEN = "```json";
    private static final String JSON_END_TOKEN = "```";

    public String extractJsonForAiQuiz(String response) {
        Predicate<String> isStartJsonMarker = i -> i.contains(JSON_START_TOKEN);
        Predicate<String> isEndJsonMarker = i -> i.contains(JSON_END_TOKEN);

        return Arrays.stream(response.split("\n"))
                .dropWhile(isStartJsonMarker.negate())
                .skip(1)
                .takeWhile(isEndJsonMarker.negate())
                .collect(Collectors.joining("\n"));
    }
}
