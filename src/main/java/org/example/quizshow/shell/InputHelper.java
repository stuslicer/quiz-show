package org.example.quizshow.shell;

import org.example.quizshow.model.Quiz;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.springframework.shell.command.CommandContext;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.example.quizshow.shell.ShellUtils.TerminalWriter.writeWith;

public class InputHelper {

    private final CommandContext ctx;
    private final LineReader lineReader;

    public InputHelper(CommandContext ctx) {
        this.ctx = ctx;
        lineReader = LineReaderBuilder.builder().terminal(ctx.getTerminal()).build();
    }

    public String readAndValidate(String prompt,
                                 String invalidPrompt,
                                 Predicate<String> validator) {
        return readAndValidate(prompt, invalidPrompt, validator, Function.identity());
    }

    /**
     * Reads input from the user and validates it using the given validator.
     * If the input is invalid, prompts the user with an error message and continues until valid input is provided.
     *
     * @param <T>             the type parameter of the return value
     * @param prompt          the prompt to display to the user
     * @param invalidPrompt   the prompt to display for invalid input
     * @param validator       the validator predicate to validate the input
     * @param mapper          the mapper function to map the input to the desired type
     * @return the validated input
     */
    public <T> T readAndValidate(String prompt,
                                 String invalidPrompt,
                                 Predicate<String> validator,
                                 Function<String, T> mapper) {
        while (true) {
            String readLine = lineReader.readLine(prompt);
            if (validator.test(readLine)) {
                return mapper.apply(readLine);
            }
            writeWith(ctx).text(invalidPrompt).write();
        }
    }


}
