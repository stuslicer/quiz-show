package org.example.quizshow.generator;

import org.example.quizshow.generator.openai.model.OpenAIRecords.ChatResponse;
import org.example.quizshow.generator.openai.model.OpenAIRecords.ChatRequest;
import org.example.quizshow.generator.openai.model.OpenAIRecords.ModelList;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Interface that represents the OpenAI API requests.
 * This will be implemented by Spring's Http Client interface magic.
 */
@HttpExchange("/v1")
public interface OpenAiInterface {

    // Models
    @GetExchange(value = "/models", accept = MediaType.APPLICATION_JSON_VALUE)
    ModelList listModels();

    // Chat
    @PostExchange(value = "/chat/completions",
            accept = MediaType.APPLICATION_JSON_VALUE,
            contentType = MediaType.APPLICATION_JSON_VALUE)
    ChatResponse getChatResponse(@RequestBody ChatRequest chatRequest);

}
