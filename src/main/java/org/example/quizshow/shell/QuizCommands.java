package org.example.quizshow.shell;


import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizResult;
import org.example.quizshow.model.QuizResultSummary;
import org.example.quizshow.runner.QuizRunner;
import org.example.quizshow.service.QuizResultRepository;
import org.example.quizshow.service.QuizService;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.example.quizshow.shell.ShellUtils.*;
import static org.example.quizshow.shell.ShellUtils.TerminalWriter.*;

@Component
@Command
public class QuizCommands {

    private final QuizService quizService;
    private final QuizResultRepository quizResultRepository;

    @Autowired
    public QuizCommands(QuizService quizService, QuizResultRepository quizResultRepository) {
        this.quizService = quizService;
        this.quizResultRepository = quizResultRepository;
    }

    @Command(description = "List all available quizzes")
    public void listQuizzes(
            CommandContext ctx,
            @Option(longNames = "filter",
                    shortNames = 'f',
                    required = false,
                    description = "filter quiz",
                    arity = CommandRegistration.OptionArity.ZERO_OR_ONE) String filter,
            @Option(longNames = "withStats",
                    shortNames = 's',
                    required = false,
                    description = "show stats for quiz runs",
                    defaultValue = "false",
                    arity = CommandRegistration.OptionArity.ZERO_OR_ONE) boolean withStats) {

        record QuizDetails(String quizId, String original, String lowercase, QuizResultSummary summary) {};

        List<QuizDetails> quizList = quizService.getAllQuizzes()
                .stream()
                .map(q -> new QuizDetails( q.getId(),q.getName(), q.getName().toLowerCase(),
                        withStats ? quizService.getQuizResultSummary(q.getId()).orElse(null) : null) )
                .toList();

        if( filter != null && !filter.isEmpty()) {
            quizList = quizList.stream().filter( q -> q.lowercase.contains(filter.toLowerCase())).toList();
        }
        quizList.forEach( quiz -> {
            String stats = quiz.summary() != null ? quiz.summary().toString() : "";
            writeWith(ctx).as(green).style(AttributedStyle.BOLD).text( quiz.original() + stats).flush(false).write();
        });
        writeWith(ctx).justFlush();
    }

    @Command(description = "Runs a quiz")
    public void run(
            CommandContext ctx,
            @Option(longNames = "quiz",
                    shortNames = 'q',
                    description = "Run the given quiz",
                    defaultValue = "f3938b67-d690-41e5-8112-ca357d18a1a9",
                    required = true) String quizId,
            @Option(longNames = "dryRun",
                    shortNames = 'd',
                    description = "Dry run, don't save results",
                    defaultValue = "false",
                    required = false) boolean dryRun
    ) {
        Optional<Quiz> quizOptional = quizService.getQuizById(quizId);

        if (quizOptional.isPresent()) {
            QuizRunner runner = new QuizRunner(ctx, quizOptional.get());

            QuizResult quizResult = runner.run();

            if( ! dryRun ) {
                quizResultRepository.addResult(quizResult);
                writeWith(ctx).text("Results written to history.");
            }
        }

    }

    @Command(description = "Displays summary statistics for quiz")
    public void summary(
            CommandContext ctx,
            @Option(longNames = "quiz",
                    shortNames = 'q',
                    description = "Run the given quiz",
                    defaultValue = "f3938b67-d690-41e5-8112-ca357d18a1a9",
                    required = true) String quizId
    ) {
        Optional<Quiz> quizOptional = quizService.getQuizById(quizId);

        if (quizOptional.isPresent()) {
            Optional<QuizResultSummary> quizResultSummary = quizService.getQuizResultSummary(quizId);

            writeWith(ctx).text(quizResultSummary.toString()).write();
        }

    }

    @Command(description = "Generate a new quiz")
    public void generateQuiz(
            CommandContext ctx,
            @Option(longNames = "prompt",
                    shortNames = 'p',
                    description = "Prompt for generating the quiz, the subject of the quiz",
                    required = true) String prompt,
            @Option(longNames = "numQuestions",
                    shortNames = 'n',
                    description = "Number of questions for the quiz",
                    defaultValue = "10"
            ) int numOfQuestions) {

        writeWith(ctx).as(magenta).text("Generating quiz... this may take a while").write();

        quizService.generateNewQuiz(prompt, numOfQuestions);

        writeWith(ctx).as(magenta).style(AttributedStyle.BOLD).text("Quiz generated!").write();
    }


}
