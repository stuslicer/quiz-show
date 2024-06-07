package org.example.quizshow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.example.quizshow.model.FileUtils;
import org.example.quizshow.model.Quiz;
import org.example.quizshow.model.QuizResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Log4j2
@Repository
public class FileBasedQuizResultRepository implements QuizResultRepository {

    private static final String RESULT_FILE_PREFIX = "results-";

    private final String baseDirectory;
    private final ObjectMapper objectMapper;

    public FileBasedQuizResultRepository(
            @Value("${quiz.repository.dir:.}") String baseDirectory,
            ObjectMapper objectMapper) {
        this.baseDirectory = baseDirectory;
        this.objectMapper = objectMapper;
    }

    @Override
    public void addResult(QuizResult result) {

        final String resultsAsJson;
        try {
            resultsAsJson = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.write(Paths.get(baseDirectory, generateQuizResultFileName(result)),
                    List.of(resultsAsJson),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<QuizResult> getResultsForQuiz(String quizId) {
        try (Stream<Path> paths = Files.list(Path.of(baseDirectory))) {
            return paths.filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().startsWith(STR."\{RESULT_FILE_PREFIX}\{quizId}"))
                    .flatMap(fileAsPath -> FileUtils.readFileAsStrings(fileAsPath).stream())
                    .map(jsonString -> convertJsonToQuizResult(jsonString))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<QuizResult> convertJsonToQuizResult(String jsonString) {
        return JsonUtils.convertJsonToObject(jsonString, QuizResult.class);
    }

    /**
     * Generates the file name for a quiz result based on the given QuizResult object.
     *
     * @param result The QuizResult object for which to generate the file name.
     * @return The generated file name as a string.
     */
    private String generateQuizResultFileName(QuizResult result) {
        return generateQuizResultFileName(result.quizId());
    }

    /**
     * Generates the file name for a quiz result based on the given quiz ID.
     *
     * @param quizId The ID of the quiz for which to generate the file name.
     * @return The generated file name as a string.
     */
    private String generateQuizResultFileName(String quizId) {
        return "%s%s.txt".formatted(RESULT_FILE_PREFIX, quizId.toLowerCase());
    }

}
