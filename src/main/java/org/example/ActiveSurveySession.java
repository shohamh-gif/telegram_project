package org.example;

import lombok.Getter;

import javax.swing.Timer;
import java.util.*;

public class ActiveSurveySession {

    private static final int REMINDER_DELAY_MS = 3 * 60 * 1000;
    private static final int SURVEY_DURATION_MS = 5 * 60 * 1000;

    @Getter
    private final Map<Long, CommunityUser> participants;
    private final Map<String, SurveyData> pollIdToQuestion;
    private final Map<String, Map<Long, Integer>> votesByPoll;
    private final Map<String, PollLocation> pollLocations;

    private final MyBot bot;
    private final DashboardFrame dashboard;
    private final long startTimeMillis;

    private boolean closed;
    private Timer reminderTimer;
    private Timer endTimer;
    private Timer uiRefreshTimer;

    public ActiveSurveySession(Map<Long, CommunityUser> currentCommunity, int totalQuestionsHint, MyBot bot, DashboardFrame dashboard) {
        this.participants = new HashMap<>(currentCommunity);
        this.pollIdToQuestion = new LinkedHashMap<>();
        this.votesByPoll = new HashMap<>();
        this.pollLocations = new HashMap<>();
        this.bot = bot;
        this.dashboard = dashboard;
        this.startTimeMillis = System.currentTimeMillis();
        this.closed = false;
        startTimers();
    }

    public synchronized void registerPollId(String pollId, SurveyData question, long chatId, int messageId) {
        this.pollIdToQuestion.put(pollId, question);
        this.votesByPoll.put(pollId, new HashMap<>());
        this.pollLocations.put(pollId, new PollLocation(chatId, messageId));
    }

    private int totalQuestions() {
        return this.pollIdToQuestion.size();
    }

    private void startTimers() {
        this.reminderTimer = new Timer(REMINDER_DELAY_MS, e -> sendReminders());
        this.reminderTimer.setRepeats(false);
        this.reminderTimer.start();

        this.endTimer = new Timer(SURVEY_DURATION_MS, e -> endSurvey());
        this.endTimer.setRepeats(false);
        this.endTimer.start();

        this.uiRefreshTimer = new Timer(1000, e -> pushStatusToUi());
        this.uiRefreshTimer.start();
        pushStatusToUi();
    }

    public synchronized void recordVote(long chatId, String pollId, List<Integer> optionIds) {
        if (this.closed || !this.participants.containsKey(chatId)) {
            return;
        }
        Map<Long, Integer> votesForPoll = this.votesByPoll.get(pollId);
        if (votesForPoll == null) {
            return;
        }

        if (optionIds == null || optionIds.isEmpty()) {
            votesForPoll.remove(chatId);
        } else {
            boolean isFirstAnswer = !votesForPoll.containsKey(chatId);
            votesForPoll.put(chatId, optionIds.get(0));

            if (isFirstAnswer) {
                PollLocation location = this.pollLocations.get(pollId);
                if (location != null) {
                    this.bot.stopPoll(location.chatId, location.messageId);
                }
            }
        }
        pushStatusToUi();
        checkIfAllFinished();
    }

    private int answeredCount(long chatId) {
        int count = 0;
        for (Map<Long, Integer> votes : this.votesByPoll.values()) {
            if (votes.containsKey(chatId)) {
                count++;
            }
        }
        return count;
    }

    private void checkIfAllFinished() {
        int total = totalQuestions();
        if (total == 0) {
            return;
        }
        for (CommunityUser user : this.participants.values()) {
            if (answeredCount(user.getChatId()) < total) {
                return;
            }
        }
        endSurvey();
    }

    private void sendReminders() {
        int total = totalQuestions();
        String reminderText = "⏰ תזכורת: הסקר עדיין פתוח! אנא השלימו את המענה בהקדם.";
        for (CommunityUser user : this.participants.values()) {
            if (answeredCount(user.getChatId()) < total) {
                this.bot.sendTextMessage(String.valueOf(user.getChatId()), reminderText);
            }
        }
    }

    private synchronized void endSurvey() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.reminderTimer.stop();
        this.endTimer.stop();
        this.uiRefreshTimer.stop();
        this.bot.setSurveyActive(false);

        for (Map.Entry<String, PollLocation> entry : this.pollLocations.entrySet()) {
            String pollId = entry.getKey();
            PollLocation loc = entry.getValue();

            Map<Long, Integer> votes = this.votesByPoll.get(pollId);
            if (votes == null || votes.isEmpty()) {
                this.bot.stopPoll(loc.chatId, loc.messageId);
            }
        }
        this.bot.onSurveyEnded();

        List<QuestionResult> results = buildResults();
        this.dashboard.showSurveyResults(results);
    }

    public void pushStatusToUi() {
        this.dashboard.updateSurveyStatus(buildSnapshot());
    }

    public synchronized SurveyStatusSnapshot buildSnapshot() {
        List<ParticipantProgress> progressList = new ArrayList<>();
        int total = totalQuestions();
        int completed = 0;
        for (CommunityUser user : this.participants.values()) {
            int answered = answeredCount(user.getChatId());
            ParticipantStatus status;
            if (total == 0 || answered == 0) {
                status = ParticipantStatus.NOT_STARTED;
            } else if (answered < total) {
                status = ParticipantStatus.IN_PROGRESS;
            } else {
                status = ParticipantStatus.COMPLETED;
                completed++;
            }
            progressList.add(new ParticipantProgress(user, answered, total, status));
        }
        long elapsed = System.currentTimeMillis() - this.startTimeMillis;
        long remainingMs = Math.max(0, SURVEY_DURATION_MS - elapsed);
        return new SurveyStatusSnapshot(progressList, this.participants.size(), completed, remainingMs);
    }

    public synchronized List<QuestionResult> buildResults() {
        List<QuestionResult> results = new ArrayList<>();
        for (Map.Entry<String, SurveyData> entry : this.pollIdToQuestion.entrySet()) {
            SurveyData question = entry.getValue();
            Map<Long, Integer> votes = this.votesByPoll.get(entry.getKey());
            int[] counts = new int[question.getAnswers().size()];
            for (int optionIdx : votes.values()) {
                if (optionIdx >= 0 && optionIdx < counts.length) {
                    counts[optionIdx]++;
                }
            }
            int totalVotes = votes.size();
            List<AnswerResult> answerResults = new ArrayList<>();
            for (int i = 0; i < counts.length; i++) {
                double pct = totalVotes == 0 ? 0.0 : (counts[i] * 100.0 / totalVotes);
                answerResults.add(new AnswerResult(question.getAnswers().get(i), counts[i], pct));
            }
            answerResults.sort((a, b) -> b.votes - a.votes);
            results.add(new QuestionResult(question.getQuestion(), answerResults));
        }
        return results;
    }
}