package org.example.quizshow.shell;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.command.CommandContext;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class ShellUtils {

    public static String withColour(String input, int styleColor) {
        AttributedStringBuilder aob = new AttributedStringBuilder();
        aob.append(input, AttributedStyle.DEFAULT.foreground(styleColor));
        return aob.toAnsi();
    }

    /**
     * Represents a base function for formatting text with colour and style.
     * This function takes an integer representing the colour, a string representing the text, and an AttributedStyle object
     * representing the style, and returns the formatted text as a string.
     *
     * {@snippet :
     * Usage:
     * Function<Integer, Function<String, Function<AttributedStyle, String>>> baseFunction =
     *         colour -> text -> style -> {
     *             AttributedStringBuilder aob = new AttributedStringBuilder();
     *             aob.append(text, style.foreground(colour));
     *             return aob.toAnsi();
     *         };
     *
     * Method: baseFunction.apply(colour).apply(text).apply(style)
     *
     * Example:
     * AttributedStyle style = AttributedStyle.DEFAULT.foreground(AttributedStyle.RED);
     * String formattedText = baseFunction.apply(255).apply("Hello").apply(style);
     * System.out.println(formattedText);
     * }
     *
     * Output: Hello (coloured and styled according to the provided colour and style)
     */
    private static Function<Integer, Function<String, Function<AttributedStyle, String>>> baseFunction =
            colour -> text -> style -> {
                AttributedStringBuilder aob = new AttributedStringBuilder();
                aob.append(text, style.foreground(colour));
                return aob.toAnsi();
            };

    /**
     * A set of curried functions to simplify adding a colour to a text.
     * The resulting function is passed the appropriate {@link AttributedStyle}
     */


    public static Function<String, Function<AttributedStyle, String>> black = baseFunction.apply(AttributedStyle.BLACK);
    public static Function<String, Function<AttributedStyle, String>> blue = baseFunction.apply(AttributedStyle.BLUE);
    public static Function<String, Function<AttributedStyle, String>> cyan = baseFunction.apply(AttributedStyle.CYAN);
    public static Function<String, Function<AttributedStyle, String>> green = baseFunction.apply(AttributedStyle.GREEN);
    public static Function<String, Function<AttributedStyle, String>> magenta = baseFunction.apply(AttributedStyle.MAGENTA);
    public static Function<String, Function<AttributedStyle, String>> red = baseFunction.apply(AttributedStyle.RED);
    public static Function<String, Function<AttributedStyle, String>> yellow = baseFunction.apply(AttributedStyle.YELLOW);

    /**
     * The Writer class provides functionality for writing text messages to a terminal.
     *
     * Follows the builder pattern to simplify the addition of colour, style and automatically flushing of writer's stream.
     */
    public static class TerminalWriter {
        private final CommandContext ctx;
        private String text;
        private boolean flush = true;
        private Function<String, Function<AttributedStyle, String>> colourText;
        private AttributedStyle style = AttributedStyle.DEFAULT.foreground(AttributedStyle.BLACK);

        private TerminalWriter(CommandContext ctx) {
            this.ctx = ctx;
        }

        public static TerminalWriter writeWith(CommandContext ctx) {
            Assert.notNull(ctx, "CommandContext must not be null");
            TerminalWriter writer = new TerminalWriter(ctx);
            return writer;
        }

        public TerminalWriter text(String text) {
            Assert.notNull(text, "Text must not be null");
            this.text = text;
            return this;
        }

        public TerminalWriter as(Function<String, Function<AttributedStyle, String>> colourText) {
            this.colourText = colourText;
            return this;
        }

        public TerminalWriter as(Function<String, Function<AttributedStyle, String>> colourText,
                                 AttributedStyle style) {
            this.colourText = colourText;
            this.style = style;
            return this;
        }

        public TerminalWriter style(AttributedStyle style) {
            this.style = style;
            return this;
        }

        public TerminalWriter flush() {
            return flush(true);
        }
        public TerminalWriter flush(boolean flush) {
            this.flush = flush;
            return this;
        }

        /**
         * Writes the text to the terminal output stream.
         * If a colourText function is provided, it applies the function to the text and style before printing.
         * If the flush flag is set to true, the writer's stream is flushed after writing the text.
         */
        public void write() {
            String toPrint = this.colourText != null ? this.colourText.apply(text).apply(style) : text;
            this.ctx.getTerminal().writer().println(toPrint);
            if( this.flush ) {
                ctx.getTerminal().writer().flush();
            }
        }

        /**
         * Convenience method to write out a newline
         */
        public void writeNewLine() {
            this.ctx.getTerminal().writer().println("\n");
            if( this.flush ) {
                ctx.getTerminal().writer().flush();
            }
        }

        /**
         * Just flushes the stream, nothing is written to the stream.
         */
        public void justFlush() {
            ctx.getTerminal().writer().flush();
        }
    }

    public static double round(double d, int scale) {
        BigDecimal bd = BigDecimal.valueOf(d).setScale(scale, RoundingMode.HALF_DOWN);
        return bd.doubleValue();
    }

}
