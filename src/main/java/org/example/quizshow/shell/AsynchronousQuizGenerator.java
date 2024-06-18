package org.example.quizshow.shell;

import org.example.quizshow.event.EventQueue;
import org.example.quizshow.event.QuizEvent;
import org.example.quizshow.event.QuizGeneratedEvent;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AsynchronousQuizGenerator {

    private final QuizService quizService;
    private final EventQueue eventQueue;
    private final ExecutorService executorService;

    @Autowired
    public AsynchronousQuizGenerator(QuizService quizService, EventQueue eventQueue) {
        this.quizService = quizService;
        this.eventQueue = eventQueue;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void generateNewQuiz(String prompt, int numOfQuestions) {
        executorService.submit(() -> {
            Quiz quiz = quizService.generateNewQuiz(prompt, numOfQuestions);
            eventQueue.enqueueEvent(new QuizGeneratedEvent(quiz.getId()));
        });
    }
}
