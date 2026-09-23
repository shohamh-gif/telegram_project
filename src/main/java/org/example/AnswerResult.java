package org.example;

public class AnswerResult {
    public final String text;
    public final int votes;
    public final double percentage;

    public AnswerResult(String text, int votes, double percentage) {
        this.text = text;
        this.votes = votes;
        this.percentage = percentage;
    }
}