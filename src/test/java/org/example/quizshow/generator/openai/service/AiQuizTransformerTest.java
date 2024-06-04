package org.example.quizshow.generator.openai.service;

import org.example.quizshow.generator.openai.model.AiQuizRecords;
import org.example.quizshow.generator.openai.model.AiQuizRecords.AiQuiz;
import static org.example.quizshow.generator.openai.model.AiQuizRecords.AiQuiz.AiQuestion;
import org.example.quizshow.model.Question;
import org.example.quizshow.model.Quiz;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiQuizTransformerTest {

    @Test
    public void shouldTransformAiQuizToQuiz() {
        // given
        AiQuiz.AiQuestion.AiQuestionOption option =
				new AiQuiz.AiQuestion.AiQuestionOption("option", true, "explanation");
        List<AiQuiz.AiQuestion.AiQuestionOption> options = List.of(option);
        AiQuiz.AiQuestion question = new AiQuiz.AiQuestion("question", options);
        List<AiQuiz.AiQuestion> questions = List.of(question);
        AiQuiz aiQuiz = new AiQuiz(questions);

        AiQuizTransformer transformer = new AiQuizTransformer();

        // when
        Quiz transformed = transformer.transform(aiQuiz);

        // then
        Assertions.assertEquals(1, transformed.getQuestions().size());

        Question transformedQuestion = transformed.getQuestions().get(0);
        Assertions.assertEquals("question", transformedQuestion.text());
        Assertions.assertEquals(1, transformedQuestion.options().size());

        Question.QuestionOption transformedOption = transformedQuestion.options().get(0);
        Assertions.assertEquals("option", transformedOption.text());
        Assertions.assertTrue(transformedOption.isCorrect());
        Assertions.assertEquals("explanation", transformedOption.explanation());
    }
}