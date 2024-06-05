package org.example.quizshow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.example.quizshow.generator.QuizConfig;
import org.example.quizshow.generator.openai.model.AiModels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class QuizConfigService {

    private static final String CONFIG_FILE_NAME = "config.json";

    private final String configDir;
    private final ObjectMapper objectMapper;

    @Autowired
    public QuizConfigService(@Value("${quiz.config.dir}") String configDir, ObjectMapper objectMapper) {
        this.configDir = configDir;
        this.objectMapper = objectMapper;
    }

    private QuizConfig quizConfig;

    public QuizConfig getQuizConfig() {
        Assert.notNull(this.quizConfig, "QuizConfig is null - probably not loaded.");
        return quizConfig;
    }

    @PostConstruct
    private void loadDefaultQuizConfig() {

        // if file exists in config directory then read
        File configFile = Paths.get(configDir, CONFIG_FILE_NAME).toFile();

        QuizConfig loadedQuizConfig = null;

        if( configFile.exists() ) {
            loadedQuizConfig = loadQuizConfig().orElse(null);
        }

        if( loadedQuizConfig == null ){
            configFile.getParentFile().mkdirs(); // we don't care about result
            loadedQuizConfig = saveQuizConfig(createDefaultQuizConfig());

            log.info(STR."Created new Quiz config file: \{configFile.getAbsolutePath()}");
        }

        this.quizConfig = loadedQuizConfig;

    }

    private String generateQuizConfigFilename() {
        return Paths.get(configDir, CONFIG_FILE_NAME).toString();
    }

    private File generateQuizConfigFile() {
        return Paths.get(configDir, CONFIG_FILE_NAME).toFile();
    }

    private Optional<QuizConfig> loadQuizConfig() {
        try {
            QuizConfig loadedQuiz = objectMapper.readValue(generateQuizConfigFile(), QuizConfig.class);
            return Optional.of(loadedQuiz);
        } catch (IOException e) {
            log.error("Failed to load QuizConfig from file: {}", generateQuizConfigFile(), e);
            return Optional.empty();
        }

    }

    private QuizConfig saveQuizConfig(QuizConfig quizConfig) {
        try {
            objectMapper.writeValue(generateQuizConfigFile(), quizConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return quizConfig;
    }

    /**
     * Creates a default QuizConfig object with predetermined values.
     *
     * @return The default QuizConfig object.
     */
    private QuizConfig createDefaultQuizConfig() {
        return new QuizConfig(AiModels.GPT_4_TURBO,
                List.of(
                        AiModels.GPT_4_TURBO,
                        AiModels.GPT_4O,
                        AiModels.GPT_4,
                        AiModels.GPT_3_5_TURBO
                ));
    }

}
