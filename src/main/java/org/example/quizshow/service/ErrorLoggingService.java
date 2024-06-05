package org.example.quizshow.service;

import org.example.quizshow.model.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ErrorLoggingService {

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");

    private String rogueDir;

    public ErrorLoggingService(@Value("${quiz.rogue.dir}") String rogueDir) {
        this.rogueDir = rogueDir;
    }

    public void logErrorWithString(Throwable throwable, String message) {
        String fileName = "%s%srogue-%s.txt".formatted(
                rogueDir,
                File.pathSeparator,
                LocalDateTime.now().format(FORMATTER)
        );
        FileUtils.writeStringToFile(fileName, STR."""
                source: \{message}
                \{throwable.toString()}
                """);
    }
}
