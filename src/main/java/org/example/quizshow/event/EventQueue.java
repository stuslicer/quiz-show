package org.example.quizshow.event;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class EventQueue {

    private Queue<QuizEvent> queue = new ConcurrentLinkedQueue<>();

    public void enqueueEvent(QuizEvent event) {
        queue.offer(event);
    }

    public Optional<QuizEvent> dequeueEvent() {
        return Optional.ofNullable(queue.poll());
    }

}
