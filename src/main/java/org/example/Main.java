package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {

        // 1. יצירת והצגת חדר הבקרה הגרפי (Swing)
        DashboardFrame dashboard = new DashboardFrame();
        dashboard.setVisible(true);

        // 2. הפעלת הבוט והעברת החלון אליו כדי שיוכל לעדכן אותו
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new MyBot(dashboard)); // שולחים לבוט את החלון שיצרנו
            System.out.println("Bot and UI are running...");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}