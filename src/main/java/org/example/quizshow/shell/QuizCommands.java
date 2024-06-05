package org.example.quizshow.shell;


import org.example.quizshow.service.QuizService;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Command
public class QuizCommands {

    private final QuizService quizService;

    @Autowired
    public QuizCommands(QuizService quizService) {
        this.quizService = quizService;
    }

    @Command(description = "List all available quizzes")
    public void listQuizzes(
            CommandContext ctx,
            @Option(longNames = "filter",
                    shortNames = 'f',
                    required = false,
                    description = "filter quiz",
                    arity = CommandRegistration.OptionArity.ZERO_OR_ONE) String filter) {

        record QuizName(String original, String lowercase) {};

        List<QuizName> quizList = quizService.getAllQuizzes()
                .stream()
                .map(q -> new QuizName(q.getName(), q.getName().toLowerCase()) )
                .toList();

        if( filter != null && !filter.isEmpty()) {
            quizList = quizList.stream().filter( q -> q.lowercase.contains(filter.toLowerCase())).toList();
        }

        quizList.forEach( quiz -> {
            ctx.getTerminal().writer().println(withColour(quiz.original(), AttributedStyle.YELLOW));
        });
        ctx.getTerminal().writer().flush();
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

        ctx.getTerminal().writer().println("Generating quiz... this may take a while");

        quizService.generateNewQuiz(prompt, numOfQuestions);

        ctx.getTerminal().writer().println("Quiz generated!");
    }

    private static String withColour(String input, int styleColor) {
        AttributedStringBuilder aob = new AttributedStringBuilder();
        aob.append(input, AttributedStyle.DEFAULT.foreground(styleColor));
        return aob.toAnsi();
    }

}
