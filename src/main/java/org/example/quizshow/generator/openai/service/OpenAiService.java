package org.example.quizshow.generator.openai.service;

import lombok.extern.log4j.Log4j2;
import org.example.quizshow.generator.openai.model.AiModels;
import org.example.quizshow.generator.openai.model.OpenAIRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class OpenAiService {

    @Autowired
    private OpenAiInterface openAiInterface;

    /**
     * Sends a chat request to the OpenAI interface and returns the response content.
     *
     * @param messages    the list of messages in the chat conversation
     * @param model       the AI model to be used for the chat request
     * @param temperature the temperature parameter for the chat request
     * @return the content of the message in the first response choice of the chat response
     */
    public String sendRequest(List<OpenAIRecords.Message> messages, AiModels model, double temperature) {
        log.debug(STR."Sending request with \{messages.size()} messages, using model \{model}");
        OpenAIRecords.ChatResponse chatResponse = openAiInterface.getChatResponse(
                new OpenAIRecords.ChatRequest(model.getId(),
                        messages, temperature));
        log.info(chatResponse);
        return chatResponse.choices().getFirst().message().content();
    }



}
