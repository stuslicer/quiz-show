package org.example.quizshow.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Quiz {

    private String id;
    private String name;
    private String description;
    private QuizDifficulty difficulty = QuizDifficulty.simple;
    private Set<String> tags = new TreeSet<>();
    private String aiPromptUsed;
    LocalDateTime generatedOn = LocalDateTime.now();
    LocalDateTime lastUpdatedOn = LocalDateTime.now();
    private List<Question> questions;

    /**
     * Compares this Quiz object with the specified object for equality.
     * The equality test is done using Quiz id only, hence need to override lombok's generated version.
     *
     * @param o the object to compare with
     * @return true if the specified object is equal to this Quiz object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return Objects.equals(id, quiz.id);
    }

    /**
     * Computes the hash code for this Quiz object.
     * The hash code is computed based on the object's ID using the Objects.hashCode method.
     *
     * @return the hash code value for this Quiz object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
