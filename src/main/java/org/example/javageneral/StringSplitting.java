package org.example.javageneral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringSplitting {

    private static final char SPACE = ' ';

    public static final String WORDS = """
            this is a long string of words that is used for a test for streaming trying to find the \
            most used word here \
            once upon a time there was a quick brown fox \
            the hitch hikers guide to the galaxy \
            out of the blue \
            new world record \
            wibble wobble willy riff raff and the rest \
            secret messages \
            """;



    static List<String> split(String text, int width) {
        List<String> splits = new ArrayList<>();
        return Arrays.stream( text.split("\n") )
                .flatMap(s -> doSplit(s, width).stream())
                .toList();
    }

    private static List<String> doSplit(String text, int width) {

        if (text.length() <= width) {
            return List.of(text);
        } else {
            int nextSplit = determineNextSplit(text, width);
            var result = new ArrayList<String>();
            result.add(text.substring(0, nextSplit));
            result.addAll(doSplit(text.substring(nextSplit).stripLeading(), width));
            return result;
        }
    }
    private static int determineNextSplit(String text, int width) {
        if (text.length() < width) {
            return text.length();
        }
        if (text.charAt(width) == ' ') {
            return width;
        }
        int nextSplit = text.lastIndexOf(' ', width);
        if (nextSplit == -1) {
            nextSplit = width;
        }
        return nextSplit;
    }

    public static void main(String[] args) {
        List<String> split = doSplit(WORDS, 40);
        System.out.println("123456789012345678901234567890123456789012345678901234567890");
        split.stream().forEach(System.out::println);
        split.stream().forEach(s -> System.out.println(s.length()));
        split = doSplit(WORDS, 50);
        System.out.println("123456789012345678901234567890123456789012345678901234567890");
        split.stream().forEach(System.out::println);
        split.stream().forEach(s -> System.out.println(s.length()));
        split = doSplit(WORDS, 60);
        System.out.println("123456789012345678901234567890123456789012345678901234567890");
        split.stream().forEach(System.out::println);
        split.stream().forEach(s -> System.out.println(s.length()));
    }

}
