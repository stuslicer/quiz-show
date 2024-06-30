package org.example.javageneral;

import java.util.ArrayList;
import java.util.List;

public class StringSplitting {

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

        if (text.length() <= width) {
            return List.of(text);
        } else {
            int nextSplit = width;
            if (text.charAt(nextSplit-1) != ' ') {
                nextSplit = text.lastIndexOf(' ', nextSplit-1);
                if (nextSplit == -1) {
                    nextSplit = width;
                }
            }
            var result = new ArrayList<String>();
            result.add(text.substring(0, nextSplit));
            result.addAll(split(text.substring(nextSplit).stripLeading(), width));
            return result;
        }
    }

    public static void main(String[] args) {
        List<String> split = split(WORDS, 51);
        System.out.println("123456789012345678901234567890123456789012345678901234567890");
        split.stream().forEach(System.out::println);
    }

}
