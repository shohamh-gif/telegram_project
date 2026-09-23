package org.example;

public class ParticipantProgress {
    public final CommunityUser user;
    public final int answered;
    public final int total;
    public final ParticipantStatus status;

    public ParticipantProgress(CommunityUser user, int answered, int total, ParticipantStatus status) {
        this.user = user;
        this.answered = answered;
        this.total = total;
        this.status = status;
    }
}