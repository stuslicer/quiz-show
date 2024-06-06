package org.example.quizshow.config;

import org.example.quizshow.generator.openai.service.OpenAiInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class ChatGPTConfiguration {

    /**
     * Creates a WebClient for accessing the OpenAI endpoint.
     * @param openAIUrl
     * @param openAIApiKey
     * @return
     */
    @Bean
    public WebClient webClient(
            @Value("${openai.baseUrl}") String openAIUrl,
            @Value("${openai.apiKey}") String openAIApiKey
    ) {

        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(openAIApiKey))
                .baseUrl(openAIUrl)
                .build();
    }

    /**
     * Creates an instance of the OpenAiInterface using the provided WebClient.
     *
     * @param webClient the WebClient used to communicate with the OpenAI endpoint
     * @return an instance of the OpenAiInterface
     */
    @Bean
    public OpenAiInterface openAiInterface(
            WebClient webClient,
            @Value("${openai.timeout.secs:120}") int timeoutSeconds) {
        WebClientAdapter adapter =WebClientAdapter.create(webClient);
        adapter.setBlockTimeout(Duration.of(timeoutSeconds, ChronoUnit.SECONDS));
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(OpenAiInterface.class);
    }

}
