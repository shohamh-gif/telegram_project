package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private final String VIEW_DASHBOARD = "Dashboard";
    private final String VIEW_SURVEY = "Survey";

    private DefaultTableModel tableModel;
    private JLabel totalMembersLabel;

    private final int WIDTH = 750;
    private final int HEIGHT = 480;
    private final int PADDING = 25;
    private final int GAP = 20;
    private final int ROW_HEIGHT = 40;

    private final String FRAME_TITLE = "מערכת ניהול סקרים - חדר בקרה";
    private final String MEMBERS_PREFIX = "סה\"כ חברים בקהילה: ";
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

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        setContentPane(mainContainer);

        mainContainer.add(createDashboardPanel(), VIEW_DASHBOARD);
        mainContainer.add(createSurveyPanel(), VIEW_SURVEY);
        cardLayout.show(mainContainer, VIEW_DASHBOARD);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(GAP, GAP));
        panel.setBackground(BACKGROUND_PINK);
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_PINK);

        totalMembersLabel = new JLabel(MEMBERS_PREFIX + "0");
        totalMembersLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        totalMembersLabel.setForeground(DARK_TEXT);

        JButton createSurveyBtn = new JButton("צור סקר חדש");
        styleButton(createSurveyBtn);
        createSurveyBtn.addActionListener(e -> cardLayout.show(mainContainer, VIEW_SURVEY));

        topPanel.add(totalMembersLabel, BorderLayout.EAST);
        topPanel.add(createSurveyBtn, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"מועד הצטרפות", "Telegram Username", "שם מלא"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSurveyPanel() {
        JPanel panel = new JPanel(new BorderLayout(GAP, GAP));
        panel.setBackground(BACKGROUND_PINK);
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        JLabel titleLabel = new JLabel("הגדרות סקר חדש", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 22));
        titleLabel.setForeground(DARK_TEXT);
        panel.add(titleLabel, BorderLayout.NORTH);

        JButton backBtn = new JButton("חזור");
        styleButton(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, VIEW_DASHBOARD));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(BACKGROUND_PINK);
        bottomPanel.add(backBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        btn.setBackground(BUTTON_PINK);
        btn.setForeground(DARK_TEXT);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false);
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