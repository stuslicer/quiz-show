package org.example.quizshow.shell;


import org.apache.commons.lang3.math.NumberUtils;
import org.example.quizshow.event.EventQueue;
import org.example.quizshow.event.QuizEvent;
import org.example.quizshow.event.QuizGeneratedEvent;
import org.example.quizshow.generator.QuizConfig;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizResult;
import org.example.quizshow.model.QuizResultSummary;
import org.example.quizshow.runner.QuizRunner;
import org.example.quizshow.service.QuizConfigService;
import org.example.quizshow.service.QuizResultRepository;
import org.example.quizshow.service.QuizService;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

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

    private enum ListActions {
        next, previous, page, nothing;
    }

    private record ListActionResult(ListActions action, int targetPage) {}


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
    private final QuizConfig quizConfig;

    private PagedList<QuizDetails> quizDetailsPagedList;

    @Autowired
    public QuizCommands(QuizService quizService, QuizResultRepository quizResultRepository, AsynchronousQuizGenerator asyncQuizGenerator, EventQueue eventQueue, QuizConfigService quizConfigService) {
        this.quizService = quizService;
        this.quizResultRepository = quizResultRepository;
        this.asyncQuizGenerator = asyncQuizGenerator;
        this.eventQueue = eventQueue;
        this.quizConfig = quizConfigService.getQuizConfig();
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
        final List<QuizDetails> quizList = quizService.getAllQuizzes()
                .stream()
                .map(q -> new QuizDetails( quizCounter.getAndIncrement(), q.getName().toLowerCase(), q,
                        withStats ? quizService.getQuizResultSummary(q.getId()).orElse(null) : null) )
                .toList();

        quizDetailsPagedList = new PagedList<>(filterDetails(quizList, filter), quizConfig.pageSize());

        while (true) {
            writeWith(ctx).text(formatPageDisplay(quizDetailsPagedList) + " quizzes").write();
            quizDetailsPagedList.forEach(details -> {
                writeWith(ctx).as(green, BOLD).text(formatQuizDisplay(details.num, details.quiz, details.summary)).flush(false).write();
            });

            ListActionResult listActionResult = getListAction(ctx, quizDetailsPagedList);

            if( listActionResult.action == ListActions.nothing) {
                break;
            }
            quizDetailsPagedList.setCurrentPage(listActionResult.targetPage);
        }
        writeWith(ctx).justFlush();
    }

    /**
     * Filters the list of QuizDetails based on a provided filter string.
     *
     * @param quizList the list of QuizDetails to filter
     * @param filter   the filter string to apply
     * @return a filtered list of QuizDetails
     */
    private List<QuizDetails> filterDetails(List<QuizDetails> quizList, String filter) {
        if( filter != null && !filter.isEmpty()) {
            return quizList.stream().filter( q -> q.lowercase.contains(filter.toLowerCase())).toList();
        }
        return quizList;
    }

    private ListActionResult getListAction(CommandContext ctx, PagedList<QuizDetails> quizDetailsPagedList) {
        if( quizDetailsPagedList.hasPreviousPage()  || quizDetailsPagedList.hasNextPage()) {
            boolean hasPrevious = quizDetailsPagedList.hasPreviousPage();
            boolean hasNext = quizDetailsPagedList.hasNextPage();
            List<String> options = new ArrayList<>();
            options.add("targetPage");
            if( hasNext ) {
                options.add("next");
            }
            if( hasPrevious ) {
                options.add("previous");
            }
            String availableOptions = String.join(", ", options);
            LineReader lineReader = LineReaderBuilder.builder().terminal(ctx.getTerminal()).build();
            String optionText = STR."Option [\{availableOptions}]: ";
            while (true) {
                String readLine = lineReader.readLine(optionText).toLowerCase();

                if (readLine.startsWith("targetPage")) {
                    String[] split = readLine.split(" +");
                    if (split.length == 2) {
                        int page = NumberUtils.toInt(split[1], -1);
                        if (page > 0 && page <= quizDetailsPagedList.totalPages()) {
                            return new ListActionResult(ListActions.page, page);
                        }
                    }
                } else if (readLine.startsWith("prev") ) {
                    if (hasPrevious) {
                        return new ListActionResult(ListActions.previous, quizDetailsPagedList.currentPage() - 1);
                    }
                } else if (readLine.startsWith("next") ) {
                    if (hasNext) {
                        return new ListActionResult(ListActions.next, quizDetailsPagedList.currentPage() + 1);
                    }
                } else {
                    return new ListActionResult(ListActions.nothing, 0);
                }
                writeWith(ctx).text("Invalid response!").write();
            }
        }
        return new ListActionResult(ListActions.nothing, 0);
    }

    /**
     * Formats the targetPage display based on the current targetPage and targetPage size of a paged list.
     * Displays in the format:
     * {@snippet :
     *    Showing 1..6
     *    Showing 1..10 of 16
     * }
     *
     * @param list the paged list
     * @return the formatted targetPage display
     */
    private String formatPageDisplay(PagedList<?> list) {
        int currentPageStart = ((list.currentPage() - 1) * list.pageSize()) + 1;
        if( list.currentPage() == 1 && list.size() <= list.pageSize()) {
            // no need for paging information
            // Showing 1..6
            return STR."Showing \{currentPageStart}..\{list.size()}";
        } else {
            int currentPageEnd = Math.min(list.size(), currentPageStart + list.pageSize() - 1);
            return STR."Showing \{currentPageStart}..\{currentPageEnd} of \{list.size()}";
        }
    }

    private Predicate<PagedList<?>> hasPreviousPagePred =
            list -> list.currentPage() > 1;

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
            return new Result<>(quizDetailsPagedList.getFullList().get(quizNumber - 1).quiz(), null);

        } else {
            // enter something or else!
            return new Result<Quiz>(null, "Please enter either an id or number to run");
        }
    }
}
