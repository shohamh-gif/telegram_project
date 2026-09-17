package org.example;

import java.util.List;

public class SurveyData {
    private String question;
    private List<String> answers;

    public SurveyData(String question, List<String> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return this.question;
    }

    public List<String> getAnswers() {
        return this.answers;
    }
}