package org.example.quizshow.shell;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShellUtilsTest {

    @Test
    public void testWithColour() {
        String input = "Hello, world!";
        int styleColor = AttributedStyle.RED;
        String expected = new AttributedStringBuilder()
                .append(input, AttributedStyle.DEFAULT.foreground(styleColor))
                .toAnsi();

        String actual = ShellUtils.withColour(input, styleColor);

        assertEquals(expected, actual);
    }

    @Test
    public void testWithDifferentColour() {
        String input = "Hello, world!";
        int styleColor = AttributedStyle.RED;
        String expected = new AttributedStringBuilder()
                .append(input, AttributedStyle.DEFAULT.foreground(styleColor))
                .toAnsi();

        String actual = ShellUtils.withColour(input, styleColor);

        assertEquals(expected, actual);
    }

    @Test
    public void testWithColourEmptyInput() {
        String input = "";
        int styleColor = AttributedStyle.RED;
        String expected = new AttributedStringBuilder()
                .append(input, AttributedStyle.DEFAULT.foreground(styleColor))
                .toAnsi();

        String actual = ShellUtils.withColour(input, styleColor);

        assertEquals(expected, actual);
    }
}