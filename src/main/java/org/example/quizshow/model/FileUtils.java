package org.example.quizshow.model;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Log4j2
public class FileUtils {

    /**
     * Reads the content of a file and returns it as a list of strings.
     *
     * @param fileName the name of the file to read
     * @return a list of strings representing the content of the file
     * @throws RuntimeException if an I/O error occurs while reading the file
     */
    public static List<String> readFileAsStrings(String fileName) {
        return readFileAsStrings(Path.of(fileName));
    }

    public static List<String> readFileAsStrings(Path fileAsPath) {
        try {
            return Files.lines(fileAsPath)
                    .toList();
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the content of a file and returns it as a string.
     *
     * @param fileName the name of the file to read
     * @return the content of the file as a string
     * @throws RuntimeException if an I/O error occurs while reading the file
     */
    public static String readFileAsString(String fileName) {
        return readFileAsString(Path.of(fileName));
    }

    public static String readFileAsString(Path fileAsPath) {
        try {
            return Files.readString(fileAsPath);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public static void writeStringToFile(String fileName, String content) {
        try {
            Files.writeString(Path.of(fileName), content);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }
}
