package org.example.quizshow;

import lombok.extern.log4j.Log4j2;
import org.example.quizshow.generator.QuizGeneratorConfig;
import org.example.quizshow.generator.openai.service.OpenAiInterface;
import org.example.quizshow.generator.openai.model.OpenAIRecords;
import org.example.quizshow.generator.openai.service.OpenAiQuizGenerator;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizDifficulty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Log4j2
@Component
public class QuizAppRunner implements CommandLineRunner {

    @Autowired
    private OpenAiInterface openAiInterface;

    @Autowired
    private OpenAiQuizGenerator openAiQuizGenerator;

    @Override
    public void run(String... args) throws Exception {
        openAiInterface.listModels().data()
                .stream()
                .filter(i -> i.id().startsWith("gpt-"))
                .sorted(Comparator.comparing(OpenAIRecords.ModelList.Model::id))
                .forEach(System.out::println);

        Quiz quiz = openAiQuizGenerator.generateQuiz(new QuizGeneratorConfig(""));
    }
}
