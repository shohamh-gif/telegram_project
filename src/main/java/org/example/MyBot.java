package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {
    private List<CommunityUser> communityUsers;

    // תיקון 1: הגדרת המשתנה ברמת המחלקה כדי שכל הפונקציות יכירו אותו
    private DashboardFrame dashboard;

    public MyBot(DashboardFrame dashboard) {
        this.communityUsers = new ArrayList<>();
        // תיקון 2: שמירת החלון שהתקבל לתוך המשתנה של המחלקה
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
                    // תיקון 3: השארנו את יצירת המשתנה רק פעם אחת, בתוך התנאי
                    CommunityUser newUser = new CommunityUser(chatId, firstName, username);
                    communityUsers.add(newUser);

                    // עכשיו זה יעבוד כי dashboard מוכר למחלקה
                    dashboard.addUserToTable(newUser);

                    // תיקון 4: קריאה לפונקציה שמעדכנת את שאר הקהילה
                    notifyOtherMembers(newUser);
                } else {
                    System.out.println("המשתמש כבר קיים בקהילה, ולכן לא יתווסף שוב.");
                }
            }
        }
    }

    private void notifyOtherMembers(CommunityUser newMember) {
        String text = "משתמש חדש הצטרף: " + newMember.getFirstName() + "\n" +
                "גודל הקהילה העדכני: " + communityUsers.size() + " חברים.";

        for (CommunityUser user : communityUsers) {
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
        for (CommunityUser user : communityUsers) {
            if (user.getChatId() == targetChatId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getBotUsername() {
        return "ShohamCodeBot";
    }

    @Override
    public String getBotToken() {
        return "8982163534:AAEDN5jpSa220jvLWJzXjov7iClAhtAVNkM";
    }
}