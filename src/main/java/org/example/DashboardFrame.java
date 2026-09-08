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

    // ==========================================
    // מסך 2: יצירת סקר
    // ==========================================
    // ==========================================
    // מסך 2: יצירת סקר
    // ==========================================
    private JPanel createSurveyPanel() {
        JPanel panel = new JPanel(new BorderLayout(GAP, GAP));
        panel.setBackground(BACKGROUND_PINK);
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        JLabel titleLabel = new JLabel("הגדרות סקר חדש", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 22));
        titleLabel.setForeground(DARK_TEXT);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout(GAP, GAP));
        formPanel.setBackground(BACKGROUND_PINK);

        // בחירת סוג סקר (הפעלת RTL כדי ש"יצירה ידנית" יהיה בצד ימין)
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.setBackground(BACKGROUND_PINK);
        radioPanel.applyComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);

        JRadioButton manualRadio = new JRadioButton("יצירה ידנית");
        JRadioButton aiRadio = new JRadioButton("ChatGPT (אוטומטי)");
        manualRadio.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        aiRadio.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        manualRadio.setBackground(BACKGROUND_PINK);
        aiRadio.setBackground(BACKGROUND_PINK);
        manualRadio.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(manualRadio);
        group.add(aiRadio);
        radioPanel.add(manualRadio);
        radioPanel.add(aiRadio);
        formPanel.add(radioPanel, BorderLayout.NORTH);

        CardLayout inputCardLayout = new CardLayout();
        JPanel inputContainer = new JPanel(inputCardLayout);
        inputContainer.setBackground(BACKGROUND_PINK);

        // תצוגה 1: טופס ידני (שימוש ב-GridLayout יציב)
        JPanel manualQuestionsContainer = new JPanel(new GridLayout(3, 1, 0, 0));
        manualQuestionsContainer.setBackground(BACKGROUND_PINK);
        manualQuestionsContainer.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        manualQuestionsContainer.add(createQuestionBlock(1));
        manualQuestionsContainer.add(createQuestionBlock(2));
        manualQuestionsContainer.add(createQuestionBlock(3));

        // פאנל עוטף
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND_PINK);
        wrapper.add(manualQuestionsContainer, BorderLayout.NORTH);

        JScrollPane manualScroll = new JScrollPane(wrapper);
        manualScroll.setBorder(null);
        manualScroll.getViewport().setBackground(BACKGROUND_PINK);
        manualScroll.getVerticalScrollBar().setUnitIncrement(16);
        manualScroll.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        inputContainer.add(manualScroll, "Manual");
        // תצוגה 2: טופס אוטומטי (הפעלת RTL)
        JPanel aiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aiPanel.setBackground(BACKGROUND_PINK);
        aiPanel.applyComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);

        aiPanel.add(new JLabel("הזן נושא לסקר:"));
        aiPanel.add(new JTextField(20));
        inputContainer.add(aiPanel, "AI");

        formPanel.add(inputContainer, BorderLayout.CENTER);

        // אזור טיימר השהיה (הפעלת RTL)
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        delayPanel.setBackground(BACKGROUND_PINK);
        delayPanel.applyComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);

        delayPanel.add(new JLabel("השהיה לפני שליחה (בדקות, 0 למיידי):"));
        JTextField delayField = new JTextField("0", 5);
        delayPanel.add(delayField);
        formPanel.add(delayPanel, BorderLayout.SOUTH);

        panel.add(formPanel, BorderLayout.CENTER);

        manualRadio.addActionListener(e -> inputCardLayout.show(inputContainer, "Manual"));
        aiRadio.addActionListener(e -> inputCardLayout.show(inputContainer, "AI"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setBackground(BACKGROUND_PINK);

        JButton sendBtn = new JButton("שלח סקר");
        styleButton(sendBtn);

        JButton backBtn = new JButton("ביטול וחזור");
        styleButton(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, VIEW_DASHBOARD));

        bottomPanel.add(sendBtn);
        bottomPanel.add(backBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createQuestionBlock(int qNum) {
        JPanel panel = new JPanel(new GridLayout(5, 1, 0, 10)); // ריווח קצת יותר גדול בין השורות
        panel.setBackground(BACKGROUND_PINK);
        panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String optionalText = (qNum > 1) ? " (אופציונלי):" : ":";
        String[] labels = {
                "שאלה " + qNum + optionalText,
                "תשובה 1:",
                "תשובה 2:",
                "תשובה 3 (אופציונלי):",
                "תשובה 4 (אופציונלי):"
        };

        for (String labelText : labels) {
            JPanel rowPanel = new JPanel(new BorderLayout(15, 0));
            rowPanel.setBackground(BACKGROUND_PINK);
            rowPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

            JLabel label = new JLabel(labelText);
            label.setPreferredSize(new Dimension(140, 20));
            rowPanel.add(label, BorderLayout.LINE_START);

            // התיקון הגדול: הגדרת 30 עמודות וגובה קבוע כדי למנוע מעיכה
            JTextField textField = new JTextField(30);
            textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 20));
            rowPanel.add(textField, BorderLayout.CENTER);

            panel.add(rowPanel);
        }

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 0, 15, 0),
                BorderFactory.createMatteBorder(0, 0, 2, 0, HEADER_PINK)
        ));

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