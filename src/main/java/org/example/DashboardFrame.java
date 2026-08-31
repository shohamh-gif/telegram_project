package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JLabel totalMembersLabel;

    private final int WIDTH = 750;
    private final int HEIGHT = 480;
    private final int PADDING = 25;
    private final int GAP = 20;
    private final int ROW_HEIGHT = 40;

    private final String FRAME_TITLE = "מערכת ניהול סקרים - חדר בקרה";
    private final String MEMBERS_PREFIX = "סה\"כ חברים בקהילה: ";
    private final String BTN_CREATE_SURVEY = "צור סקר חדש";
    private final String[] COLUMN_NAMES = {"מועד הצטרפות", "Telegram Username", "שם מלא"};
    private final String FONT_NAME = "Segoe UI";

    private final Color BACKGROUND_PINK = new Color(253, 245, 247);
    private final Color HEADER_PINK = new Color(250, 220, 228);
    private final Color BUTTON_PINK = new Color(248, 190, 205);
    private final Color DARK_TEXT = new Color(80, 80, 80);

    public DashboardFrame() {
        setTitle(FRAME_TITLE);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(GAP, GAP));
        mainPanel.setBackground(BACKGROUND_PINK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        setContentPane(mainPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_PINK);

        totalMembersLabel = new JLabel(MEMBERS_PREFIX + "0");
        totalMembersLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        totalMembersLabel.setForeground(DARK_TEXT);

        JButton createSurveyBtn = new JButton(BTN_CREATE_SURVEY);
        createSurveyBtn.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        createSurveyBtn.setBackground(BUTTON_PINK);
        createSurveyBtn.setForeground(DARK_TEXT);
        createSurveyBtn.setPreferredSize(new Dimension(140, 40));
        createSurveyBtn.setFocusPainted(false);

        topPanel.add(totalMembersLabel, BorderLayout.EAST);
        topPanel.add(createSurveyBtn, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable usersTable = new JTable(tableModel);
        usersTable.setRowHeight(ROW_HEIGHT);
        usersTable.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        usersTable.setForeground(DARK_TEXT);
        usersTable.setSelectionBackground(HEADER_PINK);
        usersTable.setSelectionForeground(DARK_TEXT);
        usersTable.setShowGrid(false);
        usersTable.setBackground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < usersTable.getColumnCount(); i++) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader tableHeader = usersTable.getTableHeader();
        tableHeader.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        tableHeader.setBackground(HEADER_PINK);
        tableHeader.setForeground(DARK_TEXT);
        tableHeader.setReorderingAllowed(false);
        tableHeader.setPreferredSize(new Dimension(100, 35));

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(HEADER_PINK, 1, true));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void addUserToTable(CommunityUser user) {
        SwingUtilities.invokeLater(() -> {
            String username = user.getTelegramUsername() != null ? "@" + user.getTelegramUsername() : "-";
            Object[] rowData = {user.getFormattedJoinTime(), username, user.getFirstName()};
            tableModel.addRow(rowData);

            totalMembersLabel.setText(MEMBERS_PREFIX + tableModel.getRowCount());
        });
    }
}