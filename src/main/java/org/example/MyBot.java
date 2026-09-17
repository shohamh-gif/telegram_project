package org.example;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class MyBot extends TelegramLongPollingBot {
    @Getter
    private Map<Long, CommunityUser> communityUsers;
    private DashboardFrame dashboard;
    @Setter
    @Getter
    private boolean isSurveyActive;

    public MyBot(DashboardFrame dashboard) {
        this.communityUsers = new HashMap<>();
        this.dashboard = dashboard;
        this.isSurveyActive = false; // אתחול מצב הסקר
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();
            String username = update.getMessage().getFrom().getUserName();

            if (messageText.equals("היי") || messageText.equalsIgnoreCase("hi") || messageText.equals("/start")) {

                if (!this.communityUsers.containsKey(chatId)) {
                    CommunityUser newUser = new CommunityUser(chatId, firstName, username);
                    this.communityUsers.put(chatId, newUser);

                    this.dashboard.addUserToTable(newUser);
                    this.notifyOtherMembers(newUser);
                } else {
                    System.out.println("המשתמש כבר קיים בקהילה.");
                }
            }
        }
    }

    private void notifyOtherMembers(CommunityUser newMember) {
        String text = "משתמש חדש הצטרף: " + newMember.getFirstName() + "\n" +
                "גודל הקהילה העדכני: " + this.communityUsers.size() + " חברים.";

        for (CommunityUser user : this.communityUsers.values()) {
            if (user.getChatId() != newMember.getChatId()) {
                SendMessage message = new SendMessage();
                message.setChatId(user.getChatId());
                message.setText(text);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    System.out.println("שגיאה בשליחת הודעה: " + e.getMessage());
                }
            }
        }
    }

    public void sendSurveyToChat(String chatId, SurveyData survey) {
        org.telegram.telegrambots.meta.api.methods.polls.SendPoll sendPoll = new org.telegram.telegrambots.meta.api.methods.polls.SendPoll();

        sendPoll.setChatId(chatId);
        sendPoll.setQuestion(survey.getQuestion()); // לוקח את השאלה מתוך האובייקט
        sendPoll.setOptions(survey.getAnswers());   // לוקח את התשובות מתוך האובייקט

        try {
            execute(sendPoll); // פקודה של טלגרם שמשגרת את הסקר
            System.out.println("הסקר שוגר בהצלחה למשתמש: " + chatId);
        } catch (TelegramApiException e) {
            System.out.println("שגיאה בשליחת הסקר לטלגרם: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // הפונקציה החדשה שמפיצה את הסקר לכל חברי הקהילה במקביל!
    public void broadcastSurvey(SurveyData survey) {
        for (CommunityUser user : this.communityUsers.values()) {
            sendSurveyToChat(String.valueOf(user.getChatId()), survey);
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