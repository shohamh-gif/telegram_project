package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {
    private List<CommunityUser> communityUsers;

    public MyBot() {
        this.communityUsers = new ArrayList<>();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();
            String username = update.getMessage().getFrom().getUserName();
            if (messageText.equals("היי") || messageText.equalsIgnoreCase("hi") || messageText.equals("/start")) {
                CommunityUser newUser = new CommunityUser(chatId, firstName, username);

                if (!communityUsers.contains(newUser)) {
                    communityUsers.add(newUser);
                    System.out.println(firstName + " הצטרף לקהילה בשעה " + newUser.getFormattedJoinTime() + "!");
                    notifyOtherMembers(newUser);
                } else {
                    System.out.println(firstName + " ניסה להצטרף שוב, אבל הוא כבר בפנים.");
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

    @Override
    public String getBotUsername() {
        return "ShohamCodeBot";
    }

    @Override
    public String getBotToken() {
        return "8982163534:AAEDN5jpSa220jvLWJzXjov7iClAhtAVNkM";
    }
}