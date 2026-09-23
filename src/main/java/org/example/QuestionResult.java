package org.example;

import java.util.List;

public class QuestionResult {
    public final String question;
    public final List<AnswerResult> answers;

    public QuestionResult(String question, List<AnswerResult> answers) {
        this.question = question;
        this.answers = answers;
    }
}