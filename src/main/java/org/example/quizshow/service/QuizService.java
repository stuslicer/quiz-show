package org.example.quizshow.service;

import lombok.extern.log4j.Log4j2;
import org.example.quizshow.generator.QuizConfig;
import org.example.quizshow.generator.QuizGenerator;
import org.example.quizshow.generator.QuizGeneratorConfig;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizDifficulty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class QuizService {

    private final QuizGenerator quizGenerator;
    private final QuizRepository quizRepository;
    private final QuizConfigService quizConfigService;

    private final QuizConfig quizConfig;

    @Autowired
    public QuizService(QuizGenerator quizGenerator, QuizRepository quizRepository, QuizConfigService quizConfigService) {
        this.quizGenerator = quizGenerator;
        this.quizRepository = quizRepository;
        this.quizConfigService = quizConfigService;

        this.quizConfig  = quizConfigService.getQuizConfig();
    }

    public Optional<Quiz> getQuizById(String id) {
        return quizRepository.loadById(id);
    }

    public Quiz generateNewQuiz(String prompt, int numberOfQuestions) {
        Quiz newQuiz = quizGenerator.generateQuiz(
                new QuizGeneratorConfig(prompt, numberOfQuestions, QuizDifficulty.medium, quizConfig.defaultAiModel()));
        log.debug(STR."New quiz created: \{newQuiz}");

        Quiz saved = quizRepository.saveQuiz(newQuiz);
        return saved;

    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

}
