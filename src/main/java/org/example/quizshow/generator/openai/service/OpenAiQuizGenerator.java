package org.example.quizshow.generator.openai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.example.quizshow.generator.QuizGenerator;
import org.example.quizshow.generator.QuizGeneratorConfig;
import org.example.quizshow.generator.openai.model.OpenAIRecords.*;
import org.example.quizshow.generator.openai.model.Role;
import org.example.quizshow.model.AiEngine;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.service.ErrorLoggingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.FormatProcessor.FMT;
import static org.example.quizshow.generator.openai.model.AiQuizRecords.AiQuiz;

@Log4j2
@Component
public class OpenAiQuizGenerator implements QuizGenerator {

    private static final String QUIZ_SETUP_PROMPT = """
            Please could you generate a multiple choice quiz suitable for a senior Java developer.
            The number of questions and exact subject will be given in the next prompt.
            Each question should have 4 options, the correct option should be indicated.
            Also where possible there should be an explanation of why that is the correct answer.
            Please format the answer in a JSON format so that it can be loaded into the following Java record classes.
            \"""
                public record AiQuiz(
                        List<AiQuestion> questions  // a list of questions for the quiz
                ) {
                        
                    public record AiQuestion(
                            String question, // the question text
                            List<AiQuestionOption> options // a list of options question
                    ) {
                        
                        public record AiQuestionOption(
                                String option, // a option for the question
                                boolean correct, // is the option the correct one?
                                String explanation // an explanation as to why its the correct option, or optionally the incorrect one
                        ) {}
                    }
                }
                        
            \"""
            """;

    private OpenAiInterface openAiInterface;
    private OpenAiService openAiService;
    private AiQuizTransformer aiQuizTransformer = new AiQuizTransformer();
    private ObjectMapper objectMapper;
    private ErrorLoggingService errorLoggingService;

    public OpenAiQuizGenerator(
            OpenAiInterface openAiInterface,
            OpenAiService openAiService,
            AiQuizTransformer aiQuizTransformer,
            ObjectMapper objectMapper,
            ErrorLoggingService errorLoggingService) {
        this.openAiInterface = openAiInterface;
        this.openAiService = openAiService;
        this.aiQuizTransformer = aiQuizTransformer;
        this.objectMapper = objectMapper;
        this.errorLoggingService = errorLoggingService;
    }

    private JsonQuizExtractor jsonQuizExtractor = new JsonQuizExtractor();

    @Override
    public Quiz generateQuiz(QuizGeneratorConfig config) {

        AiQuiz aiQuiz = generateAiQuiz(config);

        Quiz quiz = aiQuizTransformer.transform(aiQuiz);

        return populateNewQuiz(config, quiz);
    }

    /**
     * Generates an AI quiz based on the given configuration.
     *
     * @param config The configuration for generating the AI quiz.
     * @return An instance of AiQuiz generated based on the given configuration.
     */
    private AiQuiz generateAiQuiz(QuizGeneratorConfig config) {

        log.debug(STR."Creating quiz with prompt \{config.prompt()} and number of questions \{config.numberOfQuestions()}");

        Message initialPrompt = new Message(Role.USER, QUIZ_SETUP_PROMPT);
        Message quizSubject = new Message(Role.USER,
                STR."\{config.numberOfQuestions()} questions on \{config.prompt()}");

        String response = openAiService.sendRequest(List.of(initialPrompt, quizSubject), config.aiModels(), 0.7);

        return convertJsonToQuiz(jsonQuizExtractor.extractJsonForAiQuiz(response));
    }

    private Quiz populateNewQuiz(QuizGeneratorConfig config, Quiz quiz) {
        quiz.setId(UUID.randomUUID().toString());
        quiz.setName( config.prompt() );
        quiz.setDescription( config.prompt() );
        quiz.setAiEngine(AiEngine.openAI);
        quiz.setModel(config.aiModels());
        quiz.setAiPromptUsed(config.prompt());
        return quiz;
    }

    private AiQuiz convertJsonToQuiz(String jsonQuiz) {
        try {
            return objectMapper.readValue(jsonQuiz, AiQuiz.class);
        } catch (JsonProcessingException e) {
            errorLoggingService.logErrorWithString(e, jsonQuiz);
            throw new RuntimeException(e);
        }
    }
}
