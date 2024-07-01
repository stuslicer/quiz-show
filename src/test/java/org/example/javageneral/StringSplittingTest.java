package org.example.javageneral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class StringSplittingTest {

    private static final String inputString = """
                this is a long string of words that is used for a test for streaming trying to find the \
                most used word here \
                once upon a time there was a quick brown fox \
                the hitch hikers guide to the galaxy \
                out of the blue \
                new world record \
                wibble wobble willy riff raff and the rest \
                secret messages \
                """;

    /**
     * Testing the behavior of String splitting method under different scenarios.
     */
    @Test
    public void testStringSplitting() {
        final List<String> expected = Arrays.stream("""
                this is a long string of words that is used for a
                test for streaming trying to find the most used
                word here once upon a time there was a quick brown\s
                fox the hitch hikers guide to the galaxy out of
                the blue new world record wibble wobble willy riff\s
                raff and the rest secret messages\s
                """.split("\n")).toList();

        // Test when string is split with width=51
        List<String> splitNormal = StringSplitting.split(inputString, 51);
        assertEquals(6, splitNormal.size());
        assertEquals(expected, splitNormal);
        splitNormal.forEach(split -> assertTrue(split.length() <= 51));
    }

    @Test
    public void testStringSplitting5Chars() {
        final List<String> expected = Arrays.stream("""
                this 
                is a 
                long 
                strin
                g of 
                """.split("\n")).toList();

        // Test when string is split with width=1
        List<String> splitWidthOne = StringSplitting.split(inputString.substring(0, 25), 6);
        splitWidthOne.forEach(System.out::println);
        assertEquals(expected, splitWidthOne);

        // Test when string is split with width = inputString.length()
        List<String> splitFullWidth = StringSplitting.split(inputString, inputString.length());
        assertEquals(1, splitFullWidth.size());
        assertEquals(inputString, splitFullWidth.getFirst());

        // Test when string is split with width higher than inputString.length()
        List<String> splitOverWidth = StringSplitting.split(inputString, inputString.length() + 10);
        assertEquals(1, splitOverWidth.size());
        assertEquals(inputString, splitOverWidth.getFirst());
    }

    @Test
    public void testStringSplitting02() {
        final String inputString = """
                this is a long string of words that is used for a test for streaming trying to find the \
                most used word here \
                once upon a time there was a quick brown fox \
                the hitch hikers guide to the galaxy \
                out of the blue \
                new world record \
                wibble wobble willy riff raff and the rest \
                secret messages \
                """;

        // Test when string is split with width=51
        List<String> splitNormal = StringSplitting.split(inputString, 51);
        assertEquals(6, splitNormal.size());
        splitNormal.forEach(split -> assertTrue(split.length() <= 51));


        // Test when string is split with width=1
        List<String> splitWidthOne = StringSplitting.split(inputString, 5);
        assertEquals(inputString.length(), splitWidthOne.size());
        splitWidthOne.forEach(split -> assertEquals(1, split.length()));

        // Test when string is split with width = inputString.length()
        List<String> splitFullWidth = StringSplitting.split(inputString, inputString.length());
        assertEquals(1, splitFullWidth.size());
        assertEquals(inputString, splitFullWidth.getFirst());

        // Test when string is split with width higher than inputString.length()
        List<String> splitOverWidth = StringSplitting.split(inputString, inputString.length() + 10);
        assertEquals(1, splitOverWidth.size());
        assertEquals(inputString, splitOverWidth.getFirst());
    }
}