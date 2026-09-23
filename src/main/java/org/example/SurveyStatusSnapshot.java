package org.example;

import java.util.List;

public class SurveyStatusSnapshot {
    public final List<ParticipantProgress> progressList;
    public final int totalParticipants;
    public final int completedCount;
    public final long remainingMs;

    public SurveyStatusSnapshot(List<ParticipantProgress> progressList, int totalParticipants, int completedCount, long remainingMs) {
        this.progressList = progressList;
        this.totalParticipants = totalParticipants;
        this.completedCount = completedCount;
        this.remainingMs = remainingMs;
    }
}