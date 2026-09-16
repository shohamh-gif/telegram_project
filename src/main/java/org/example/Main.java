package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. קודם יוצרים את המסך הגרפי
                DashboardFrame dashboard = new DashboardFrame();

                // 2. עכשיו יוצרים את הבוט ומעבירים לו את המסך כמו שהוא ציפה!
                MyBot myBot = new MyBot(dashboard);

                // 3. משדכים ביניהם - אומרים למסך מי זה הבוט שלו
                dashboard.setBot(myBot);

                // 4. רושמים את הבוט לשרתים של טלגרם
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(myBot);

                System.out.println("הבוט התחבר בהצלחה לטלגרם!");

                // 5. מציגים את המסך
                dashboard.setVisible(true);

            } catch (Exception e) {
                System.out.println("שגיאה בהפעלת התוכנית: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}