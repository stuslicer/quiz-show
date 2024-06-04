package org.example.quizshow.generator.openai.service;

import org.example.quizshow.generator.openai.model.AiQuizRecords;
import org.example.quizshow.model.Question;
import org.example.quizshow.model.Quiz;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.example.quizshow.generator.openai.model.AiQuizRecords.*;
import static org.example.quizshow.generator.openai.model.AiQuizRecords.AiQuiz.*;
import static org.example.quizshow.model.Question.*;

/**
 * Transforms a {@link AiQuiz} object to the {@link Quiz} object.
 */
@Component
public class AiQuizTransformer {

    public Quiz transform(AiQuiz aiQuiz) {

        AtomicInteger questionCounter = new AtomicInteger(0);
        List<Question> questions = aiQuiz.questions().stream()
                .map(aiQuestion -> transform(aiQuestion, questionCounter.incrementAndGet()))
                .toList();

        Quiz quiz = new Quiz();
        quiz.setQuestions(questions);
        return quiz;
    }

    private Question transform(AiQuestion aiQuestion, int questionNumber) {
        List<QuestionOption> options = aiQuestion.options().stream()
                .map(this::transform)
                .toList();
        return new Question(questionNumber, aiQuestion.question(), options);
    }

    private QuestionOption transform(AiQuestion.AiQuestionOption aiQuestionOption) {
        return new QuestionOption(aiQuestionOption.option(), aiQuestionOption.correct(), aiQuestionOption.explanation());
    }

}
