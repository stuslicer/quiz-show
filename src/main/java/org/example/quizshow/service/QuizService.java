package org.example.quizshow.service;

import lombok.extern.log4j.Log4j2;
import org.example.quizshow.generator.QuizConfig;
import org.example.quizshow.generator.QuizGenerator;
import org.example.quizshow.generator.QuizGeneratorConfig;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizDifficulty;
import org.example.quizshow.model.QuizResult;
import org.example.quizshow.model.QuizResultSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class QuizService {

    private final QuizGenerator quizGenerator;
    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizConfigService quizConfigService;

    private final QuizConfig quizConfig;

    @Autowired
    public QuizService(QuizGenerator quizGenerator, QuizRepository quizRepository, QuizResultRepository quizResultRepository, QuizConfigService quizConfigService) {
        this.quizGenerator = quizGenerator;
        this.quizRepository = quizRepository;
        this.quizResultRepository = quizResultRepository;
        this.quizConfigService = quizConfigService;

        this.quizConfig  = quizConfigService.getQuizConfig();
    }

    public Optional<Quiz> getQuizById(String id) {
        return quizRepository.loadById(id);
    }

    public void addResult(QuizResult result) {
        quizResultRepository.addResult(result);
    }

    public Optional<QuizResultSummary> getQuizResultSummary(String quizId) {

        List<QuizResult> resultsForQuiz = quizResultRepository.getResultsForQuiz(quizId);

        ResultCalculator collected = resultsForQuiz.stream()
                .collect(ResultCalculator::new,
                        ResultCalculator::append,
                        (summary1, summary2) -> {
                            // not required - no parallelism here!
                        }
                );

        return collected.getSummary();
    }

    public Quiz generateNewQuiz(String prompt, int numberOfQuestions) {
        log.debug("About to generate a new quiz!");
        Quiz newQuiz = quizGenerator.generateQuiz(
                new QuizGeneratorConfig(prompt, numberOfQuestions, QuizDifficulty.medium, quizConfig.defaultAiModel()));
        log.debug(STR."New quiz created: \{newQuiz}");

        return quizRepository.saveQuiz(newQuiz);

    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    class ResultCalculator {
        private String quizId;
        private int count;
        private int perfect;
        private double timeMean;
        private double successPercentageMean;
        private double successSumOfSquares;
        private LocalDateTime lastPlayed;

        public void append(QuizResult quizResult) {
            this.quizId = quizResult.quizId();
            count++;
            if( quizResult.isPerfect() ) perfect++;
            double timeDelta = quizResult.getDuration() - timeMean;
            timeMean += timeDelta / count;
            double successDelta = quizResult.getSuccessPercentage() - successPercentageMean;
            successPercentageMean += successDelta / count;
            successSumOfSquares += successDelta * successDelta;
            lastPlayed = lastPlayed == null ? LocalDateTime.now() : lastPlayed.isBefore(quizResult.endTime()) ? quizResult.endTime() : lastPlayed;
        }

        public Optional<QuizResultSummary> getSummary() {
            return this.quizId != null ?
                    Optional.of(new QuizResultSummary(
                            quizId,
                            count,
                            perfect,
                            successPercentageMean,
                            timeMean,
                            lastPlayed
                    )) : Optional.empty();
        }
    }


}
