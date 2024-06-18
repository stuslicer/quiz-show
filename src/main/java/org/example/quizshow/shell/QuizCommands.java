package org.example.quizshow.shell;


import org.example.quizshow.event.EventQueue;
import org.example.quizshow.event.QuizEvent;
import org.example.quizshow.event.QuizGeneratedEvent;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizResult;
import org.example.quizshow.model.QuizResultSummary;
import org.example.quizshow.runner.QuizRunner;
import org.example.quizshow.service.QuizResultRepository;
import org.example.quizshow.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.FormatProcessor.FMT;
import static org.example.quizshow.shell.ShellUtils.*;
import static org.example.quizshow.shell.ShellUtils.TerminalWriter.*;
import static org.jline.utils.AttributedStyle.*;

@Component
@Command
public class QuizCommands {

    /**
     * QuizDetails represents the details of a quiz, including the number, lowercase name, quiz object, and quiz result summary.
     */
    private record QuizDetails(int num, String lowercase, Quiz quiz, QuizResultSummary summary) {};

    /**
     * Result is a generic class representing a result that can have a value or an error message.
     * This is a concept from functional programming, where the value can represent either a successful operation or a failed
     * operation - typically an exception or error messaage. By convention the left hand side represents a failure
     * and right success.
     *
     * @param <T> the type of the value
     */
    private record Result<T>(T result, String error) {
        public boolean isPresent() {
            return result != null;
        }
    }


    private final QuizService quizService;
    private final QuizResultRepository quizResultRepository;
    private final AsynchronousQuizGenerator asyncQuizGenerator;
    private final EventQueue eventQueue;

    private int pageSize = 100;
    private PagedList<QuizDetails> quizDetailsPagedList;

    @Autowired
    public QuizCommands(QuizService quizService, QuizResultRepository quizResultRepository, AsynchronousQuizGenerator asyncQuizGenerator, EventQueue eventQueue) {
        this.quizService = quizService;
        this.quizResultRepository = quizResultRepository;
        this.asyncQuizGenerator = asyncQuizGenerator;
        this.eventQueue = eventQueue;
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
                    arity = CommandRegistration.OptionArity.ZERO_OR_ONE) boolean withStats
    ) {

        handleAnyEvents(ctx);

        final AtomicInteger quizCounter = new AtomicInteger(1);
        List<QuizDetails> quizList = quizService.getAllQuizzes()
                .stream()
                .map(q -> new QuizDetails( quizCounter.getAndIncrement(), q.getName().toLowerCase(), q,
                        withStats ? quizService.getQuizResultSummary(q.getId()).orElse(null) : null) )
                .toList();

        if( filter != null && !filter.isEmpty()) {
            quizList = quizList.stream().filter( q -> q.lowercase.contains(filter.toLowerCase())).toList();
        }
        quizDetailsPagedList = new PagedList<>(quizList, pageSize);
        quizDetailsPagedList.forEach( details -> {
            writeWith(ctx).as(green, BOLD).text( formatQuizDisplay(details.num, details.quiz, details.summary)).flush(false).write();
        });
        writeWith(ctx).justFlush();
    }

    private String formatQuizDisplay(int number, Quiz quiz, QuizResultSummary summary) {
        String stats = STR.", \{formatQuizResultSummary(summary)}";
        return FMT."%3s\{number}. \{quiz.getName()} (\{quiz.getQuestions().size()}), created: \{quiz.getGeneratedOn().format(formatter)}\{stats}";
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, d MMM");

    private String formatQuizResultSummary(QuizResultSummary summary) {
        return summary != null ? FMT."""
            last played: \{summary.lastPlayed().format(formatter)}, \
            stats - runs: %d\{summary.count()}, \
            perfect: %d\{summary.perfect()}, \
            avg success: %3.2f\{summary.averageSuccessRate()} %%, \
            avg time: %3.2f\{summary.averageTime()} secs, \
            """  : "";
    }

    @Command(description = "Run a quiz")
    public void run(
            CommandContext ctx,
            @Option(longNames = "quiz",
                    shortNames = 'q',
                    description = "Run the given quiz using id",
                    required = false) String quizId,
            @Option(longNames = "number",
                    shortNames = 'n',
                    description = "Run the given quiz using number",
                    required = false) int quizNumber,
            @Option(longNames = "dryRun",
                    shortNames = 'd',
                    description = "Dry run, don't save results",
                    defaultValue = "false",
                    required = false) boolean dryRun
    ) {
        handleAnyEvents(ctx);
        Result<Quiz> lookupResult = lookupQuiz(quizId, quizNumber);
        if( ! lookupResult.isPresent()) {
            writeWith(ctx).as(red).text(lookupResult.error).write();
            return;
        }
        Quiz quiz = lookupResult.result;

        QuizRunner runner = new QuizRunner(ctx, quiz);

        QuizResult quizResult = runner.run();

        if (!dryRun) {
            quizResultRepository.addResult(quizResult);
            writeWith(ctx).text("Results written to history.");
        }

        Optional<QuizResultSummary> quizResultSummary = quizService.getQuizResultSummary(quiz.getId());
        if( quizResultSummary.isPresent()) {
            String statsFormatted = formatQuizResultSummary(quizResultSummary.get());
            writeWith(ctx).text(statsFormatted).write();
        }

    }

    @Command(description = "Displays summary statistics for quiz")
    public void summary(
            CommandContext ctx,
            @Option(longNames = "quiz",
                    shortNames = 'q',
                    description = "Run the given quiz",
                    defaultValue = "f3938b67-d690-41e5-8112-ca357d18a1a9",
                    required = false) String quizId,
            @Option(longNames = "number",
                    shortNames = 'n',
                    description = "Run the given quiz using number",
                    required = false) int quizNumber
    ) {
        handleAnyEvents(ctx);
        Result<Quiz> lookupResult = lookupQuiz(quizId, quizNumber);
        if( ! lookupResult.isPresent()) {
            writeWith(ctx).as(red).text(lookupResult.error).write();
            return;
        }
        Quiz quiz = lookupResult.result;

        Optional<QuizResultSummary> quizResultSummary = quizService.getQuizResultSummary(quiz.getId());

        writeWith(ctx).text(quizResultSummary.toString()).write();
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
            ) int numOfQuestions,
            @Option(longNames = "foreground",
                    shortNames = 'f',
                    description = "Generate the quiz in the foreground",
                    defaultValue = "false"
            ) boolean foreground
    ) {

        if( foreground ) {
            writeWith(ctx).as(magenta).text("Generating quiz... this may take a while").write();

            quizService.generateNewQuiz(prompt, numOfQuestions);

            writeWith(ctx).as(magenta, BOLD).text("Quiz generated!").write();
        } else {
            writeWith(ctx).as(magenta).text("Generating quiz in background").write();

            asyncQuizGenerator.generateNewQuiz(prompt, numOfQuestions);

            writeWith(ctx).as(magenta, BOLD).text("Request submitted").write();
        }

    }

    private void handleAnyEvents(CommandContext ctx) {
        while (true) {
            Optional<QuizEvent> event = eventQueue.dequeueEvent();
            if( event.isEmpty()) {
                break;
            }
            switch (event.get()) {
                case QuizGeneratedEvent qe -> handleQuizGeneratedEvent(qe, ctx);
            }
        }
    }

    private void handleQuizGeneratedEvent(QuizGeneratedEvent quizGeneratedEvent, CommandContext ctx) {
        Optional<Quiz> quizById = quizService.getQuizById(quizGeneratedEvent.getQuizId());
        quizById.ifPresent(quiz ->
                writeWith(ctx).as(magenta, BOLD).text(STR."Quiz generated for \{quiz.getName()}!").write()
        );

    }

    /**
     * Looks up a quiz based on the provided ID or number.
     *
     * @param quizId      the ID of the quiz (optional)
     * @param quizNumber  the number of the quiz (optional)
     * @return a Result object containing the found Quiz or an error message
     */
    private Result<Quiz> lookupQuiz(String quizId, int quizNumber) {
        if (quizId != null && !quizId.isEmpty()) {
            Optional<Quiz> quizOptional = quizService.getQuizById(quizId);
            return quizOptional.map(value -> new Result<>(value, null))
                    .orElseGet(() -> new Result<>(null, "Quiz doesn't exist"));

        } else if (quizNumber > 0) {
            if( quizNumber > quizDetailsPagedList.size() ) {
                return new Result<Quiz>(null, STR."Sorry but \{quizNumber} is too high!");
            }
            return new Result<>(quizDetailsPagedList.getList().get(quizNumber - 1).quiz(), null);

        } else {
            // enter something or else!
            return new Result<Quiz>(null, "Please enter either an id or number to run");
        }
    }
}
