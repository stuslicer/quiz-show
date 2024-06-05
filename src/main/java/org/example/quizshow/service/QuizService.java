package org.example.quizshow.service;

import lombok.extern.log4j.Log4j2;
import org.example.quizshow.generator.QuizGenerator;
import org.example.quizshow.generator.QuizGeneratorConfig;
import org.example.quizshow.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class QuizService {

    private QuizGenerator quizGenerator;
    private QuizRepository quizRepository;

    @Autowired
    public QuizService(QuizGenerator quizGenerator, QuizRepository quizRepository) {
        this.quizGenerator = quizGenerator;
        this.quizRepository = quizRepository;
    }

    public Quiz generateNewQuiz(String prompt, int numberOfQuestions) {
        Quiz newQuiz = quizGenerator.generateQuiz(new QuizGeneratorConfig(prompt, numberOfQuestions));
        log.debug(STR."New quiz created: \{newQuiz}");

        Quiz saved = quizRepository.saveQuiz(newQuiz);
        return saved;

    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

}
