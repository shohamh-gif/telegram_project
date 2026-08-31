package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {

        // --- בדיקה קצרה של מחלקת המשתמש שלנו ---
        CommunityUser testUser = new CommunityUser(112233L, "Shoham", "shoham_h");

        System.out.println("New user created!");
        System.out.println("Name: " + testUser.getFirstName());
        System.out.println("Username: @" + testUser.getTelegramUsername());
        System.out.println("Join time: " + testUser.getFormattedJoinTime()); // זה המבחן האמיתי!
        System.out.println("----------------------------------");

        // --- הקוד המקורי של הבוט ---
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new MyBot());
            System.out.println("Bot is running...");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}