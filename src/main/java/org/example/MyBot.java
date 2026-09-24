package org.example;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.polls.StopPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBot extends TelegramLongPollingBot {
    @Getter
    private Map<Long, CommunityUser> communityUsers;
    private DashboardFrame dashboard;
    @Setter
    @Getter
    private boolean isSurveyActive;
    private ActiveSurveySession currentSession;

    public MyBot(DashboardFrame dashboard) {
        this.communityUsers = new HashMap<>();
        this.dashboard = dashboard;
        this.isSurveyActive = false;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update.getMessage());
        } else if (update.hasPollAnswer()) {
            handlePollAnswer(update.getPollAnswer());
        }
    }

    private void handleTextMessage(Message message) {
        String messageText = message.getText();
        long chatId = message.getChatId();
        String firstName = message.getFrom().getFirstName();
        String username = message.getFrom().getUserName();

        if (messageText.equals("היי") || messageText.equalsIgnoreCase("hi") || messageText.equals("/start")) {
            if (!this.communityUsers.containsKey(chatId)) {
                CommunityUser newUser = new CommunityUser(chatId, firstName, username);
                this.communityUsers.put(chatId, newUser);
                this.dashboard.addUserToTable(newUser);
                this.notifyOtherMembers(newUser);
                this.sendTextMessage(String.valueOf(chatId),
                        "ברוך/ה הבא/ה לקהילה! 🎉 כאן תקבל/י התראה בכל פעם שייפתח סקר חדש.");
            } else {
                System.out.println("המשתמש כבר קיים בקהילה.");
            }
        }
    }

    private void handlePollAnswer(PollAnswer pollAnswer) {
        if (this.currentSession == null) {
            return;
        }
        long chatId = pollAnswer.getUser().getId();
        String pollId = pollAnswer.getPollId();
        this.currentSession.recordVote(chatId, pollId, pollAnswer.getOptionIds());
    }

    private void notifyOtherMembers(CommunityUser newMember) {
        String text = "משתמש חדש הצטרף: " + newMember.getFirstName() + "\n" +
                "גודל הקהילה העדכני: " + this.communityUsers.size() + " חברים.";

        for (CommunityUser user : this.communityUsers.values()) {
            if (user.getChatId() != newMember.getChatId()) {
                this.sendTextMessage(String.valueOf(user.getChatId()), text);
            }
        }
    }

    public void startSurvey(List<SurveyData> questions) {
        this.currentSession = new ActiveSurveySession(this.communityUsers, questions.size(), this, this.dashboard);
        this.isSurveyActive = true;
        this.dashboard.onSurveyStarted(this.currentSession);

        new Thread(() -> {
            for (CommunityUser user : this.currentSession.getParticipants().values()) {
                for (SurveyData question : questions) {
                    sendSurveyToChat(String.valueOf(user.getChatId()), question);
                }
            }
        }, "survey-dispatch-thread").start();
    }

    private void sendSurveyToChat(String chatId, SurveyData survey) {
        SendPoll sendPoll = new SendPoll();
        sendPoll.setChatId(chatId);
        sendPoll.setQuestion(survey.getQuestion());
        sendPoll.setOptions(survey.getAnswers());
        sendPoll.setIsAnonymous(false);
        sendPoll.setAllowMultipleAnswers(false);

        try {
            Message sentMessage = execute(sendPoll);
            String pollId = sentMessage.getPoll().getId();
            int messageId = sentMessage.getMessageId();
            if (this.currentSession != null) {
                this.currentSession.registerPollId(pollId, survey, Long.parseLong(chatId), messageId);
            }
        } catch (TelegramApiException e) {
            System.out.println("שגיאה בשליחת הסקר לטלגרם: " + e.getMessage());
        }
    }

    public void stopPoll(long chatId, int messageId) {
        StopPoll stopPoll = new StopPoll();
        stopPoll.setChatId(String.valueOf(chatId));
        stopPoll.setMessageId(messageId);
        try {
            execute(stopPoll);
        } catch (TelegramApiException e) {
            System.out.println("שגיאה בסגירת ה-poll האישי: " + e.getMessage());
        }
    }

    public void onSurveyEnded() {
        this.currentSession = null;
    }

    public void sendTextMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "SHOAM1_BOT";
    }

    @Override
    public String getBotToken() {
        return Utils.getSecureToken("BOT_TOKEN");
    }
}