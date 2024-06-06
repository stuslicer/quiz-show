package org.example.quizshow.runner;

import lombok.extern.log4j.Log4j2;
import org.example.quizshow.model.Question;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizResults;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.springframework.shell.command.CommandContext;

import java.util.ArrayList;
import java.util.List;

import static org.example.quizshow.shell.ShellUtils.*;

@Log4j2
public class QuizRunner {

    private CommandContext ctx;

    private LineReader lineReader;

    public QuizRunner(CommandContext ctx) {
        this.ctx = ctx;
        lineReader = LineReaderBuilder.builder().terminal(ctx.getTerminal()).build();
    }

    public QuizResults run(Quiz quiz) {

        log.info("Quiz is running");

        List<Question> questions = new ArrayList<>(quiz.getQuestions());

        int questionNumber = 1;
        int correctAnswers = 0;
        for (Question question : questions) {

            // print out question
            Writer.with(ctx).text(STR."\{questionNumber}. \{question.text()}").write();

            for(Question.QuestionOption option : question.options()) {
                Writer.with(ctx).text(option.text()).write();
            }

            // get response from user
            // validate

            String usersAnswer = lineReader.readLine("Enter your response: ");

            Writer.with(ctx).text(usersAnswer).write();

            questionNumber++;
        }



        return new QuizResults();
    }

}
