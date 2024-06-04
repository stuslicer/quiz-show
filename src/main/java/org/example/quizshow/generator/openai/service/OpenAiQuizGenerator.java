package org.example.quizshow.generator.openai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.example.quizshow.generator.QuizGenerator;
import org.example.quizshow.generator.QuizGeneratorConfig;
import org.example.quizshow.generator.openai.model.AiModels;
import org.example.quizshow.generator.openai.model.AiQuizRecords;
import org.example.quizshow.generator.openai.model.OpenAIRecords.*;
import org.example.quizshow.generator.openai.model.Role;
import org.example.quizshow.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.example.quizshow.generator.openai.model.AiQuizRecords.*;

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

    @Autowired
    private OpenAiInterface openAiInterface;
    @Autowired
    private OpenAiService openAiService;
    @Autowired
    private ObjectMapper objectMapper;

    private JsonQuizExtractor jsonQuizExtractor = new JsonQuizExtractor();

    @Override
    public Quiz generateQuiz(QuizGeneratorConfig config) {

        // ask initial setup question

        // pass in the question

        // get result

        Message simpleHello = new Message(Role.USER, "Hello, how are you today?");
        Message initialPrompt = new Message(Role.USER, QUIZ_SETUP_PROMPT);
        Message quizSubject = new Message(Role.USER, "6 questions on records in Java 17");

        String response = openAiService.sendRequest(List.of(initialPrompt, quizSubject), AiModels.GPT_4_TURBO, 0.7);

        // extract JSON

        System.out.println(response);

        return null;
    }

    public AiQuiz loadJsonQuiz(String jsonQuiz) {
        try {
            AiQuiz aiQuiz = objectMapper.readValue(jsonQuiz, AiQuiz.class);
            return aiQuiz;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
