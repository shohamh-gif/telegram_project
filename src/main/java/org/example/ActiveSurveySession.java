package org.example;

import javax.swing.Timer;
import java.util.*;

/**
 * מנהל סשן של סקר פעיל אחד: משתתפים, מיפוי poll_id -> שאלה, הצבעות בפועל,
 * טיימרים (תזכורת/סיום), ובניית סנאפשוט חי + תוצאות סופיות.
 */
public class ActiveSurveySession {

    public enum ParticipantStatus { NOT_STARTED, IN_PROGRESS, COMPLETED }

    private static final int REMINDER_DELAY_MS = 3 * 60 * 1000; // 3 דקות
    private static final int SURVEY_DURATION_MS = 5 * 60 * 1000; // 5 דקות

    private final Map<Long, CommunityUser> participants;        // תמונת מצב של הקהילה ברגע תחילת הסקר
    private final Map<String, SurveyData> pollIdToQuestion;     // סדר השאלות נשמר (LinkedHashMap)
    private final Map<String, Map<Long, Integer>> votesByPoll;  // pollId -> (chatId -> אינדקס תשובה שנבחרה)
    private final Map<String, PollLocation> pollLocations;      // pollId -> היכן ה-poll הזה נמצא בטלגרם (כדי לסגור אותו)

    private final MyBot bot;
    private final DashboardFrame dashboard;
    private final long startTimeMillis;
    private volatile boolean closed = false;

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

        startTimers();
    }

    public Map<Long, CommunityUser> getParticipants() {
        return this.participants;
    }

    /** נקרא מ-MyBot מיד אחרי ששאלה נשלחה בהצלחה לטלגרם וקיבלנו poll_id + מיקום ההודעה */
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

        // מרענן את הממשק כל שנייה כדי שה-Countdown וההתקדמות יהיו "חיים"
        this.uiRefreshTimer = new Timer(1000, e -> pushStatusToUi());
        this.uiRefreshTimer.start();
        pushStatusToUi();
    }

    /** נקרא מ-MyBot כאשר מתקבל PollAnswer מטלגרם */
    public synchronized void recordVote(long chatId, String pollId, List<Integer> optionIds) {
        if (this.closed || !this.participants.containsKey(chatId)) {
            return; // משתמש שהצטרף אחרי תחילת הסקר - לא רלוונטי לסקר הזה
        }
        Map<Long, Integer> votesForPoll = this.votesByPoll.get(pollId);
        if (votesForPoll == null) {
            return; // poll_id שלא שייך לסשן הזה
        }

        if (optionIds == null || optionIds.isEmpty()) {
            votesForPoll.remove(chatId); // המשתמש ביטל את הבחירה שלו בסקר הטלגרם
        } else {
            boolean isFirstAnswer = !votesForPoll.containsKey(chatId);
            votesForPoll.put(chatId, optionIds.get(0)); // תשובה יחידה לכל שאלה

            // סוגרים מיידית את ה-poll האישי הזה כדי לממש "אין לאפשר לענות פעם נוספת" (סעיף 7)
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
                return; // מישהו עוד לא סיים
            }
        }
        endSurvey(); // כולם השלימו את כל השאלות - סוגרים מוקדם
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
            answerResults.sort((a, b) -> b.votes - a.votes); // ממוין לפי שכיחות, הכי גבוה קודם
            results.add(new QuestionResult(question.getQuestion(), answerResults));
        }
        return results;
    }

    // ---- מבני עזר להעברת מידע לממשק ----

    /** מיקום הודעת ה-poll הספציפית בטלגרם (chatId + messageId), נדרש כדי לסגור אותה עם StopPoll */
    private static class PollLocation {
        final long chatId;
        final int messageId;

        PollLocation(long chatId, int messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }
    }

    public static class ParticipantProgress {
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

    public static class SurveyStatusSnapshot {
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

    public static class AnswerResult {
        public final String text;
        public final int votes;
        public final double percentage;

        public AnswerResult(String text, int votes, double percentage) {
            this.text = text;
            this.votes = votes;
            this.percentage = percentage;
        }
    }

    public static class QuestionResult {
        public final String question;
        public final List<AnswerResult> answers; // ממוין כבר לפי שכיחות

        public QuestionResult(String question, List<AnswerResult> answers) {
            this.question = question;
            this.answers = answers;
        }
    }
}