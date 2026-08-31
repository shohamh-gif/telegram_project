package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JLabel totalMembersLabel;

    // --- פלטת הצבעים לעיצוב ---
    private Color backgroundPink = new Color(255, 235, 240); // ורוד פסטל בהיר לרקע
    private Color buttonPink = new Color(255, 143, 171); // ורוד חזק לכפתורים
    private Color headerPink = new Color(255, 182, 193); // ורוד לכותרת של הטבלה
    private Color darkText = new Color(70, 70, 70); // אפור כהה לטקסט (קריא יותר משחור)

    public DashboardFrame() {
        setTitle("מערכת ניהול סקרים - חדר בקרה");
        setSize(750, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // אמצע המסך

        // פאנל ראשי עם ריווח כדי שהכל ינשום
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(backgroundPink);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // --- אזור עליון: כפתור ומונה ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(backgroundPink);

        totalMembersLabel = new JLabel("סה\"כ חברים בקהילה: 0");
        totalMembersLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalMembersLabel.setForeground(darkText);

        // כפתור מעוצב בסגנון שטוח (כמו שביקשת)
        JButton createSurveyBtn = new JButton("צור סקר חדש");
        createSurveyBtn.setFont(new Font("Arial", Font.BOLD, 16));
        createSurveyBtn.setBackground(buttonPink);
        createSurveyBtn.setForeground(Color.WHITE);
        createSurveyBtn.setFocusPainted(false); // מבטל את הריבוע המכוער של הלחיצה
        createSurveyBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // מגדיל את הכפתור

        topPanel.add(totalMembersLabel, BorderLayout.EAST);
        topPanel.add(createSurveyBtn, BorderLayout.WEST); // נוסיף לו פונקציונליות בהמשך
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- אזור מרכזי: טבלת הקהילה מעוצבת ---
        String[] columnNames = {"מועד הצטרפות", "Telegram Username", "שם מלא"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable usersTable = new JTable(tableModel);
        usersTable.setRowHeight(35); // שורות רחבות
        usersTable.setFont(new Font("Arial", Font.PLAIN, 15));
        usersTable.setForeground(darkText);
        usersTable.setSelectionBackground(buttonPink); // צבע בחירה בטבלה
        usersTable.setSelectionForeground(Color.WHITE);
        usersTable.setShowVerticalLines(false); // מראה נקי יותר בלי קווים לאורך

        // עיצוב כותרת הטבלה
        JTableHeader tableHeader = usersTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 15));
        tableHeader.setBackground(headerPink);
        tableHeader.setForeground(darkText);
        tableHeader.setReorderingAllowed(false);

        // מוסיף גלילה אם יש הרבה אנשים
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(headerPink, 2)); // מסגרת ורודה לטבלה
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void addUserToTable(CommunityUser user) {
        SwingUtilities.invokeLater(() -> {
            String username = user.getTelegramUsername() != null ? "@" + user.getTelegramUsername() : "אין";
            Object[] rowData = {user.getFormattedJoinTime(), username, user.getFirstName()};
            tableModel.addRow(rowData);
            totalMembersLabel.setText("סה\"כ חברים בקהילה: " + tableModel.getRowCount());
        });
    }
}