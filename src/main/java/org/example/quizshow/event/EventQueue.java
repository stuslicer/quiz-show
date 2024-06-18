package org.example.quizshow.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
@Component
public class EventQueue {

    private Queue<QuizEvent> queue = new ConcurrentLinkedQueue<>();

    public void enqueueEvent(QuizEvent event) {
        log.debug("Enqueuing event {}!", event);
        queue.offer(event);
    }

    public Optional<QuizEvent> dequeueEvent() {
        log.debug("Dequeuing event!");
        return Optional.ofNullable(queue.poll());
    }

}
