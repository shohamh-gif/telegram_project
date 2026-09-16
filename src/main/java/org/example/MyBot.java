package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {
    private List<CommunityUser> communityUsers;
    private DashboardFrame dashboard;

    public MyBot(DashboardFrame dashboard) {
        this.communityUsers = new ArrayList<>();
        this.dashboard = dashboard;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();
            String username = update.getMessage().getFrom().getUserName();

            if (messageText.equals("היי") || messageText.equalsIgnoreCase("hi") || messageText.equals("/start")) {

                if (!isUserExists(chatId)) {
                    CommunityUser newUser = new CommunityUser(chatId, firstName, username);
                    this.communityUsers.add(newUser);
                    this.dashboard.addUserToTable(newUser);
                    this.notifyOtherMembers(newUser);
                } else {
                    System.out.println("המשתמש כבר קיים בקהילה, ולכן לא יתווסף שוב.");
                }
            }
        }
    }

    private void notifyOtherMembers(CommunityUser newMember) {
        String text = "משתמש חדש הצטרף: " + newMember.getFirstName() + "\n" +
                "גודל הקהילה העדכני: " + this.communityUsers.size() + " חברים.";

        for (CommunityUser user : this.communityUsers) {
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

    private boolean isUserExists(long targetChatId) {
        for (CommunityUser user : this.communityUsers) {
            if (user.getChatId() == targetChatId) {
                return true;
            }
        }
        return false;
    }

    public void sendSurveyToChat(String chatId, SurveyData survey) {
        org.telegram.telegrambots.meta.api.methods.polls.SendPoll sendPoll = new org.telegram.telegrambots.meta.api.methods.polls.SendPoll();

        sendPoll.setChatId(chatId);
        sendPoll.setQuestion(survey.getQuestion()); // לוקח את השאלה מתוך האובייקט
        sendPoll.setOptions(survey.getAnswers());   // לוקח את התשובות מתוך האובייקט

        try {
            execute(sendPoll); // פקודה של טלגרם שמשגרת את הסקר
            System.out.println("הסקר שוגר לטלגרם בהצלחה!");
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
            System.out.println("שגיאה בשליחת הסקר לטלגרם: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "ShohamCodeBot";
    }

    @Override
    public String getBotToken() {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            return prop.getProperty("BOT_TOKEN");
        } catch (IOException ex) {
            System.out.println("שגיאה בקריאת קובץ הטוקן: " + ex.getMessage());
            return null;
        }
    }
}