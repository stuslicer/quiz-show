package org.example.quizshow;

import org.example.quizshow.generator.OpenAiInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class QuizAppRunner implements CommandLineRunner {

    @Autowired
    private OpenAiInterface openAiInterface;

    @Override
    public void run(String... args) throws Exception {
        openAiInterface.listModels().data().forEach(System.out::println);
    }
}
