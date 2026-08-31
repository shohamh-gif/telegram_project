package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JLabel totalMembersLabel;

    public DashboardFrame() {
        setTitle("מערכת ניהול סקרים - חדר בקרה");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15)); // הוספת ריווח (Padding) כדי שלא ייראה צפוף
        setLocationRelativeTo(null); // פותח את החלון בדיוק באמצע המסך

        // --- אזור עליון: מונה משתמשים ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalMembersLabel = new JLabel("סה\"כ חברים בקהילה: 0");
        totalMembersLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalMembersLabel.setForeground(new Color(41, 128, 185)); // צבע כחול נעים
        topPanel.add(totalMembersLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- אזור מרכזי: טבלת הקהילה ---
        // שמות העמודות בדיוק לפי דרישות הפרויקט (מסודר מימין לשמאל)
        String[] columnNames = {"מועד הצטרפות", "Telegram Username", "שם מלא"};

        // מודל הטבלה שולט בנתונים
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // מונע מהמשתמש לערוך את הטקסט בטבלה בטעות
            }
        };

        JTable usersTable = new JTable(tableModel);

        // עיצוב הטבלה ל-UX מושלם
        usersTable.setRowHeight(30); // שורות מרווחות ונוחות לקריאה
        usersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        usersTable.getTableHeader().setBackground(new Color(236, 240, 241));
        usersTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(usersTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    // הפונקציה הזו היא הקסם - הבוט יקרא לה בכל פעם שמישהו אומר "היי"
    public void addUserToTable(CommunityUser user) {
        // מוקש 100: עדכון UI חייב לקרות דרך ה-Thread של Swing!
        SwingUtilities.invokeLater(() -> {
            String username = user.getTelegramUsername() != null ? "@" + user.getTelegramUsername() : "אין";

            // מוסיפים את השורה החדשה לטבלה
            Object[] rowData = {user.getFormattedJoinTime(), username, user.getFirstName()};
            tableModel.addRow(rowData);

            // מעדכנים את המונה למעלה
            int currentCount = tableModel.getRowCount();
            totalMembersLabel.setText("סה\"כ חברים בקהילה: " + currentCount);
        });
    }
}