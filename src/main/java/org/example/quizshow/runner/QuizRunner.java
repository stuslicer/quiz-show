package org.example.quizshow.runner;

import lombok.extern.log4j.Log4j2;
import org.example.quizshow.model.Question;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizResult;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.command.CommandContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.example.quizshow.model.Question.*;
import static org.example.quizshow.shell.ShellUtils.*;
import static org.example.quizshow.shell.ShellUtils.TerminalWriter.*;

@Log4j2
public class QuizRunner {

    private static final int OPTION_INDENT = 4;

    private final CommandContext ctx;

    private final LineReader lineReader;

    private final String paddedIndent;

    public QuizRunner(CommandContext ctx) {
        this.ctx = ctx;
        lineReader = LineReaderBuilder.builder().terminal(ctx.getTerminal()).build();
        paddedIndent = String.format("%-" + OPTION_INDENT + "s", " ");
    }

    public QuizResult run(Quiz quiz) {

        record OptionHolder(int number, QuestionOption option) {};

        log.info("Quiz is running");

        List<Question> questions = new ArrayList<>(quiz.getQuestions());

        final LocalDateTime startTime = LocalDateTime.now();
        int questionNumber = 1;
        int correctAnswers = 0;
        for (Question question : questions) {

            // print out question
            writeWith(ctx).as(black, AttributedStyle.BOLD).text(STR."\{questionNumber}. \{question.text()}\n").write();

            AtomicInteger optionNumber = new AtomicInteger(1);
            List<OptionHolder> optionHolders = question.options().stream()
                    .map( o -> new OptionHolder(optionNumber.getAndIncrement(), o))
                    .toList();

            // Print out those numbers
            for(OptionHolder holder : optionHolders) {
                writeWith(ctx).text(formatQuestionOption(holder.option(), holder.number())).write();
            }

            String prompt = STR."\nEnter your response [1-\{question.options().size()}]: ";
            String invalidResponse = STR."Response must be an number between 1-\{question.options().size()}!";

            int usersAnswer = readAndValidate(prompt, invalidResponse,
                    (text) -> isInteger.test(text) &&
                            validateIntegerRange(Integer.parseInt(text), 1, questions.size()),
                    Integer::parseInt);

            QuestionOption usersOption = question.options().get(usersAnswer - 1);
            OptionHolder correctOption = optionHolders.stream().filter(h -> h.option.isCorrect()).findFirst().get();

            if( usersOption.isCorrect() ) {
                correctAnswers++;
                writeWith(ctx).as(green).text("Correct!").write();
                if( usersOption.explanation() != null) {
                    writeWith(ctx).as(green).text(STR."Explanation: \{usersOption.explanation()}").write();
                }
            } else {
                writeWith(ctx).as(red, AttributedStyle.BOLD).text(STR."Wrong!! Correct answer is (\{correctOption.number()}) \{correctOption.option().text()}").write();
                if( usersOption.explanation() != null) {
                    writeWith(ctx).as(red).text(STR."Explanation: \{usersOption.explanation()}").write();
                }
            }
            writeWith(ctx).writeNewLine();

            questionNumber++;
        }

        writeWith(ctx).as(black).text(STR."\nScore: \{correctAnswers} / \{quiz.getQuestions().size()}").write();

        if( correctAnswers == questions.size() ) {
            writeWith(ctx).as(green).text("\nCongratulations you got all answers correct!").write();
        } else {
            writeWith(ctx).as(red).text("\nSorry you didn't answer all the questions correctly!").write();
        }

        return new QuizResult(
                quiz.getId(),
                quiz.getQuestions().size(),
                correctAnswers,
                startTime,
                LocalDateTime.now()
        );
    }

    private String formatQuestionOption(QuestionOption option, int optionNumber) {
        String padded = Arrays.stream(option.text().split("\n")).map(t -> paddedIndent + t)
                .collect(Collectors.joining("\n"));
        return STR." \{optionNumber}) \{padded.substring(OPTION_INDENT)}";
    }

    public static boolean validateIntegerRange(int i, int min, int max) {
        return i >= min && i <= max;
    }

    public static Predicate<String> isInteger = (input) -> {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    };

    private <T> T readAndValidate(String prompt, String invalidPrompt,
                                   Predicate<String> validator,
                                   Function<String, T> mapper) {
        String readLine = null;
        while (true) {
            readLine = lineReader.readLine(prompt);
            if( validator.test(readLine) ) {
                return mapper.apply(readLine);
            }
            writeWith(ctx).text(invalidPrompt).write();
        }
    }

}
