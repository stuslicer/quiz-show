package org.example.quizshow.generator.openai.model;

import java.util.List;

import static org.example.quizshow.generator.openai.model.AiQuizRecords.AiQuiz.*;
import static org.example.quizshow.generator.openai.model.AiQuizRecords.AiQuiz.AiQuestion.*;

public class AiQuizRecords {

    public record AiQuiz(
            List<AiQuestion> questions
    ) {

        public record AiQuestion(
                String question,
                List<AiQuestionOption> options
        ) {

            public record AiQuestionOption(
                    String option,
                    boolean correct,
                    String explanation
            ) {}
        }
    }

    public static void main(String[] args) {
        AiQuiz aiQuiz = new AiQuiz(
                List.of(
                        new AiQuestion(
                                "What is the answer to the question",
                                    List.of(
                                            new AiQuestionOption(
                                            "Option 1",
                                            true,
                                            "explanation"
                                             ),
                                            new AiQuestionOption(
                                            "Option 2",
                                            false,
                                            "explanation"
                                             ),
                                            new AiQuestionOption(
                                            "Option 3",
                                            false,
                                            "explanation"
                                             ),
                                            new AiQuestionOption(
                                            "Option 4",
                                            false,
                                            "explanation"
                                             )
                                    )
                        )
                )
        );

    };

}
