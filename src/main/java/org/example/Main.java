package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DashboardFrame dashboard = new DashboardFrame();

                MyBot myBot = new MyBot(dashboard);

                dashboard.setBot(myBot);

                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(myBot);

                System.out.println("הבוט התחבר בהצלחה לטלגרם!");

                dashboard.setVisible(true);

            } catch (Exception e) {
                System.out.println("שגיאה בהפעלת התוכנית: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}