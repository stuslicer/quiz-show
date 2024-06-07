package org.example.quizshow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.example.quizshow.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
public class FileBasedQuizRepository implements QuizRepository {

    private static final String QUIZ_FILE_PREFIX = "quiz-";

    private final String baseDirectory;

    private final ObjectMapper objectMapper;

    @Autowired
    public FileBasedQuizRepository(@Value("${quiz.repository.dir}") String baseDirectory,
                                   ObjectMapper objectMapper) {
        this.baseDirectory = baseDirectory;
        this.objectMapper = objectMapper;
    }

    /**
     * Find all {@link Quiz}s in the file repo.
     * The resulting list is sorted by name.
     * @return
     */
    @Override
    public List<Quiz> findAll() {
        Path baseDirectory = Paths.get(this.baseDirectory);
        try (Stream<Path> paths = Files.list(baseDirectory)) {
            return paths
                    .map(Path::toFile)
//                    .peek(file -> System.out.println(file.getName()))
                    .filter(file -> file.getName().startsWith(QUIZ_FILE_PREFIX))
                    .map(this::convertJsonToQuiz)
//                    .peek(System.out::println)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing(Quiz::getName))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Quiz> loadById(String id) {
        Assert.notNull(id, "Quiz id must be set.");

        File file = new File(this.baseDirectory, generateQuizFileName(id));
        return convertJsonToQuiz(file);
    }

    private Optional<Quiz> convertJsonToQuiz(File jsonFile) {
        return JsonUtils.convertJsonToObject(jsonFile, Quiz.class);
//        try {
//            Quiz loadedQuiz = objectMapper.readValue(jsonFile, Quiz.class);
//            return Optional.of(loadedQuiz);
//        } catch (IOException e) {
//            log.error("Failed to load quiz from file: {}", jsonFile.getAbsolutePath(), e);
//            return Optional.empty();
//        }
    }

    @Override
    public Quiz saveQuiz(Quiz quiz) {
        Assert.notNull(quiz, "Quiz must not be null");
        Assert.notNull(quiz.getId(), "Quiz id must be set.");

        try {
            File file = new File(this.baseDirectory, generateQuizFileName(quiz));
            quiz.setLastUpdatedOn(LocalDateTime.now());
            objectMapper.writeValue(file, quiz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return quiz;
    }

    @Override
    public Quiz updateQuiz(Quiz quiz) {
        return saveQuiz(quiz);
    }

    @Override
    public boolean deleteQuiz(String id) {
        Assert.notNull(id, "Quiz id must be set.");

        Optional<Quiz> loadedQuiz = loadById(id);

        if (loadedQuiz.isPresent()) {
            File file = new File(this.baseDirectory, generateQuizFileName(loadedQuiz.get()));
            return file.delete();
        }
        return false;
    }

    /**
     * Generates a file name for a quiz based on the quiz ID.
     *
     * @param quiz the quiz
     * @return the generated file name in the format "quiz-{quizId}.json"
     */
    private String generateQuizFileName(Quiz quiz) {
        return generateQuizFileName(quiz.getId());
    }

    /**
     * Generates a file name for a quiz based on the quiz ID.
     *
     * @param quizId the ID of the quiz
     * @return the generated file name in the format "quiz-{quizId}.json"
     */
    private String generateQuizFileName(String quizId) {
        return "%s%s.json".formatted(QUIZ_FILE_PREFIX, quizId.toLowerCase());
    }
}
