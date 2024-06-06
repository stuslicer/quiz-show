package org.example.quizshow.shell;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.command.CommandContext;

import java.util.function.Function;

public class ShellUtils {

    public static String withColour(String input, int styleColor) {
        AttributedStringBuilder aob = new AttributedStringBuilder();
        aob.append(input, AttributedStyle.DEFAULT.foreground(styleColor));
        return aob.toAnsi();
    }

    /**
     * A base function that takes an integer representing a colour and a string of text
     * and returns a formatted string with the text coloured according to the specified colour.
     */
    private static Function<Integer, Function<String, String>> baseFunction =
            colour -> text -> {
                AttributedStringBuilder aob = new AttributedStringBuilder();
                aob.append(text, AttributedStyle.DEFAULT.foreground(colour));
                return aob.toAnsi();
            };

    public static Function<String, String> black = baseFunction.apply(AttributedStyle.BLACK);
    public static Function<String, String> blue = baseFunction.apply(AttributedStyle.BLUE);
    public static Function<String, String> cyan = baseFunction.apply(AttributedStyle.CYAN);
    public static Function<String, String> green = baseFunction.apply(AttributedStyle.GREEN);
    public static Function<String, String> magenta = baseFunction.apply(AttributedStyle.MAGENTA);
    public static Function<String, String> red = baseFunction.apply(AttributedStyle.RED);
    public static Function<String, String> yellow = baseFunction.apply(AttributedStyle.YELLOW);

    public static class Writer {
        private final CommandContext ctx;
        private String text;
        private boolean flush = true;
        private Function<String, String> colourText;

        private Writer(CommandContext ctx) {
            this.ctx = ctx;
        }

        public static Writer with(CommandContext ctx) {
            Writer writer = new Writer(ctx);
            return writer;
        }

        public Writer text(String text) {
            this.text = text;
            return this;
        }

        public Writer as(Function<String, String> colourText) {
            this.colourText = colourText;
            return this;
        }

        public Writer flush() {
            return flush(true);
        }
        public Writer flush(boolean flush) {
            this.flush = flush;
            return this;
        }

        public void write() {
            String toPrint = this.colourText != null ? this.colourText.apply(text) : text;
            this.ctx.getTerminal().writer().println(toPrint);
            if( this.flush ) {
                ctx.getTerminal().writer().flush();
            }
        }

        public void justFlush() {
            ctx.getTerminal().writer().flush();
        }
    }
}
