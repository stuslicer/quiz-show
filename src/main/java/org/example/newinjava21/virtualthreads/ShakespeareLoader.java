package org.example.newinjava21.virtualthreads;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Log4j2
public class ShakespeareLoader {

    private static final String FILE_PATH = "./src/main/resources/some_works_of_shakespeare.txt";

    public static Stream<String> getWorksAsLines() {
        try {
            return Files.lines(Path.of(FILE_PATH))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .filter(StringUtils::hasLength)
                    ;
        } catch (IOException e) {
            log.error(e);
            return Stream.empty();
        }
    }

    public static Stream<String> getWorksAsWords() {
        return getWorksAsLines()
                .map(i -> i.replaceAll("[.,-?:;!']",""))
                .map(String::toLowerCase)
                .flatMap(line -> Stream.of(line.split("\\s+")));
    }

    public static void main(String[] args) {
        getWorksAsWords().limit(100).forEach(System.out::println);
    }

}
