package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private DefaultTableModel tableModel;
    private JLabel totalMembersLabel;

    private final String VIEW_DASHBOARD = "Dashboard";
    private final String VIEW_SURVEY = "Survey";
    private final String MEMBERS_PREFIX = "סה\"כ חברים בקהילה: ";
    private final String FONT_NAME = "Segoe UI";

    private final Color BACKGROUND_PINK = new Color(253, 245, 247);
    private final Color HEADER_PINK = new Color(250, 220, 228);
    private final Color BUTTON_PINK = new Color(248, 190, 205);
    private final Color DARK_TEXT = new Color(80, 80, 80);

    public DashboardFrame() {
        this.setTitle("מערכת ניהול סקרים - חדר בקרה");
        this.setSize(750, 480);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(this.cardLayout);
        this.setContentPane(this.mainContainer);

        this.mainContainer.add(this.createDashboardPanel(), this.VIEW_DASHBOARD);
        this.mainContainer.add(this.createSurveyPanel(), this.VIEW_SURVEY);

        this.cardLayout.show(this.mainContainer, this.VIEW_DASHBOARD);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(this.BACKGROUND_PINK);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        panel.add(this.createTopPanel(), BorderLayout.NORTH);
        panel.add(this.createTablePanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(this.BACKGROUND_PINK);

        this.totalMembersLabel = new JLabel(this.MEMBERS_PREFIX + "0");
        this.totalMembersLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 18));
        this.totalMembersLabel.setForeground(this.DARK_TEXT);

        JButton createSurveyBtn = this.createStyledButton("צור סקר חדש");
        createSurveyBtn.addActionListener(e -> this.cardLayout.show(this.mainContainer, this.VIEW_SURVEY));

        topPanel.add(this.totalMembersLabel, BorderLayout.EAST);
        topPanel.add(createSurveyBtn, BorderLayout.WEST);
        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"מועד הצטרפות", "Telegram Username", "שם מלא"};
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable usersTable = new JTable(this.tableModel);
        usersTable.setRowHeight(40);
        usersTable.setFont(new Font(this.FONT_NAME, Font.PLAIN, 14));
        usersTable.setForeground(this.DARK_TEXT);
        usersTable.setSelectionBackground(this.HEADER_PINK);
        usersTable.setSelectionForeground(this.DARK_TEXT);
        usersTable.setShowGrid(false);
        usersTable.setBackground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < usersTable.getColumnCount(); i++) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader tableHeader = usersTable.getTableHeader();
        tableHeader.setFont(new Font(this.FONT_NAME, Font.BOLD, 14));
        tableHeader.setBackground(this.HEADER_PINK);
        tableHeader.setForeground(this.DARK_TEXT);
        tableHeader.setReorderingAllowed(false);
        tableHeader.setPreferredSize(new Dimension(100, 35));

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(this.HEADER_PINK, 1, true));
        return scrollPane;
    }

    private JPanel createSurveyPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(this.BACKGROUND_PINK);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel("הגדרות סקר חדש", SwingConstants.CENTER);
        titleLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 22));
        titleLabel.setForeground(this.DARK_TEXT);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout(20, 20));
        formPanel.setBackground(this.BACKGROUND_PINK);

        CardLayout inputCardLayout = new CardLayout();
        JPanel inputContainer = new JPanel(inputCardLayout);
        inputContainer.setBackground(this.BACKGROUND_PINK);

        inputContainer.add(this.createManualPanel(), "Manual");
        inputContainer.add(this.createAIPanel(), "AI");

        formPanel.add(this.createRadioPanel(inputCardLayout, inputContainer), BorderLayout.NORTH);
        formPanel.add(inputContainer, BorderLayout.CENTER);
        formPanel.add(this.createDelayPanel(), BorderLayout.SOUTH);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(this.createBottomButtonsPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRadioPanel(CardLayout inputCardLayout, JPanel inputContainer) {
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.setBackground(this.BACKGROUND_PINK);
        radioPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JRadioButton manualRadio = new JRadioButton("יצירה ידנית");
        JRadioButton aiRadio = new JRadioButton("ChatGPT (אוטומטי)");

        manualRadio.setFont(new Font(this.FONT_NAME, Font.PLAIN, 16));
        aiRadio.setFont(new Font(this.FONT_NAME, Font.PLAIN, 16));
        manualRadio.setBackground(this.BACKGROUND_PINK);
        aiRadio.setBackground(this.BACKGROUND_PINK);
        manualRadio.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(manualRadio);
        group.add(aiRadio);
        radioPanel.add(manualRadio);
        radioPanel.add(aiRadio);

        manualRadio.addActionListener(e -> inputCardLayout.show(inputContainer, "Manual"));
        aiRadio.addActionListener(e -> inputCardLayout.show(inputContainer, "AI"));

        return radioPanel;
    }

    private JScrollPane createManualPanel() {
        JPanel manualQuestionsContainer = new JPanel(new GridLayout(3, 1, 0, 0));
        manualQuestionsContainer.setBackground(this.BACKGROUND_PINK);
        manualQuestionsContainer.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        manualQuestionsContainer.add(this.createQuestionBlock(1));
        manualQuestionsContainer.add(this.createQuestionBlock(2));
        manualQuestionsContainer.add(this.createQuestionBlock(3));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(this.BACKGROUND_PINK);
        wrapper.add(manualQuestionsContainer, BorderLayout.NORTH);

        JScrollPane manualScroll = new JScrollPane(wrapper);
        manualScroll.setBorder(null);
        manualScroll.getViewport().setBackground(this.BACKGROUND_PINK);
        manualScroll.getVerticalScrollBar().setUnitIncrement(16);
        manualScroll.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        return manualScroll;
    }

    private JPanel createAIPanel() {
        JPanel aiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aiPanel.setBackground(this.BACKGROUND_PINK);
        aiPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        aiPanel.add(new JLabel("הזן נושא לסקר:"));
        aiPanel.add(new JTextField(20));
        return aiPanel;
    }

    private JPanel createDelayPanel() {
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        delayPanel.setBackground(this.BACKGROUND_PINK);
        delayPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        delayPanel.add(new JLabel("השהיה לפני שליחה (בדקות, 0 למיידי):"));
        delayPanel.add(new JTextField("0", 5));
        return delayPanel;
    }

    private JPanel createBottomButtonsPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setBackground(this.BACKGROUND_PINK);

        JButton sendBtn = this.createStyledButton("שלח סקר");
        JButton backBtn = this.createStyledButton("ביטול וחזור");

        backBtn.addActionListener(e -> this.cardLayout.show(this.mainContainer, this.VIEW_DASHBOARD));

        bottomPanel.add(sendBtn);
        bottomPanel.add(backBtn);
        return bottomPanel;
    }

    private JPanel createQuestionBlock(int qNum) {
        JPanel panel = new JPanel(new GridLayout(5, 1, 0, 10));
        panel.setBackground(this.BACKGROUND_PINK);
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
            rowPanel.setBackground(this.BACKGROUND_PINK);
            rowPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

            JLabel label = new JLabel(labelText);
            label.setPreferredSize(new Dimension(140, 35));
            rowPanel.add(label, BorderLayout.LINE_START);

            JTextField textField = new JTextField(30);
            textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 35));
            rowPanel.add(textField, BorderLayout.CENTER);

            panel.add(rowPanel);
        }

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 0, 15, 0),
                BorderFactory.createMatteBorder(0, 0, 2, 0, this.HEADER_PINK)
        ));

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(this.FONT_NAME, Font.BOLD, 14));
        btn.setBackground(this.BUTTON_PINK);
        btn.setForeground(this.DARK_TEXT);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    public void addUserToTable(CommunityUser user) {
        SwingUtilities.invokeLater(() -> {
            String username = user.getTelegramUsername() != null ? "@" + user.getTelegramUsername() : "-";
            Object[] rowData = {user.getFormattedJoinTime(), username, user.getFirstName()};
            this.tableModel.addRow(rowData);
            this.totalMembersLabel.setText(this.MEMBERS_PREFIX + this.tableModel.getRowCount());
        });
    }
}