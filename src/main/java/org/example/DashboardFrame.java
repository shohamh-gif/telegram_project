package org.example;

import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private DefaultTableModel tableModel;
    private JLabel totalMembersLabel;
    private SurveyApiService apiService;
    private JTextField aiTopicField;
    private List<JTextField> allManualFields;
    private JTextField delayField;
    private JLabel countdownLabel;
    @Setter
    private MyBot bot;

    private final String VIEW_DASHBOARD = "Dashboard";
    private final String VIEW_SURVEY = "Survey";
    private final String MEMBERS_PREFIX = "סה\"כ חברים בקהילה: ";
    private final String FONT_NAME = "Segoe UI";

    private final Color BACKGROUND_PINK = new Color(253, 245, 247);
    private final Color HEADER_PINK = new Color(250, 220, 228);
    private final Color BUTTON_PINK = new Color(248, 190, 205);
    private final Color DARK_TEXT = new Color(80, 80, 80);

    public DashboardFrame() {
        this.apiService = new SurveyApiService();
        this.allManualFields = new ArrayList<>();
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
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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
        this.aiTopicField = new JTextField(20);
        aiPanel.add(this.aiTopicField);
        return aiPanel;
    }

    private JPanel createDelayPanel() {
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        delayPanel.setBackground(this.BACKGROUND_PINK);
        delayPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        delayPanel.add(new JLabel("השהיה לפני שליחה (בדקות, 0 למיידי):"));
        this.delayField = new JTextField("0", 5);
        delayPanel.add(this.delayField);
        this.countdownLabel = new JLabel("");
        this.countdownLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 16));
        this.countdownLabel.setForeground(Color.RED);
        delayPanel.add(this.countdownLabel);
        return delayPanel;
    }

    private JPanel createBottomButtonsPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setBackground(this.BACKGROUND_PINK);

        JButton sendBtn = this.createStyledButton("שלח סקר");
        JButton backBtn = this.createStyledButton("ביטול וחזור");

        backBtn.addActionListener(e -> this.cardLayout.show(this.mainContainer, this.VIEW_DASHBOARD));

        sendBtn.addActionListener(e -> {
            if (this.bot.getCommunityUsers().size() < 3) {
                JOptionPane.showMessageDialog(this, "לא ניתן להתחיל סקר. נדרשים לפחות 3 חברים בקהילה!", "חסימה", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.bot.isSurveyActive()) {
                JOptionPane.showMessageDialog(this, "יש סקר פעיל (או בהשהיה) כרגע! חובה להמתין לסיומו.", "חסימה", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // שולפים את הדקות שהמשתמש הקליד
            int delayMinutes = 0;
            try {
                delayMinutes = Integer.parseInt(this.delayField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "נא להזין מספר דקות תקין!", "שגיאה", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (delayMinutes > 0) {
                startCountdown(delayMinutes, sendBtn);
            } else {
                executeSurveyDispatch(sendBtn); // שליחה מיידית
            }
        });
        bottomPanel.add(sendBtn);
        bottomPanel.add(backBtn);
        return bottomPanel;
    }

        // פונקציית העזר שמנתבת איזה סקר לשלוח (AI או ידני)
        private void executeSurveyDispatch(JButton sendBtn) {
            if (this.aiTopicField.isShowing()) {
                handleAISurvey(sendBtn);
            } else {
                handleManualSurvey();
            }
        }

        // הפונקציה שמפעילה את הספירה לאחור על המסך
        private void startCountdown(int minutes, JButton sendBtn) {
            this.bot.setSurveyActive(true); // נועלים כדי שלא יתחילו סקרים אחרים בזמן ההמתנה
            sendBtn.setEnabled(false);

            int totalSeconds = minutes * 60;
            int[] timeLeft = {totalSeconds}; // מערך של איבר אחד כדי שנוכל לערוך אותו מתוך הטיימר

            Timer timer = new Timer(1000, null); // טיימר שרץ כל 1000 מילי-שניות (שנייה)
            timer.addActionListener(e -> {
                if (timeLeft[0] > 0) {
                    int mins = timeLeft[0] / 60;
                    int secs = timeLeft[0] % 60;
                    this.countdownLabel.setText(String.format("הסקר יישלח בעוד: %02d:%02d", mins, secs));
                    timeLeft[0]--;
                } else {
                    ((Timer)e.getSource()).stop();
                    this.countdownLabel.setText(""); // מנקים את השעון
                    executeSurveyDispatch(sendBtn); // הזמן נגמר - משגרים את הסקר!
                }
            });
            timer.start();
        }



    // פונקציית עזר פרטית 1: ניתוב וטיפול בסקר AI
    private void handleAISurvey(JButton sendBtn) {
        String topic = this.aiTopicField.getText().trim();
        if (topic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "נא להזין נושא לסקר", "שגיאה", JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.bot.setSurveyActive(true);
        sendBtn.setEnabled(false);
        sendBtn.setText("מייצר סקר...");

        new AiSurveyTask(topic, this.apiService, this.bot, this, sendBtn).execute();
    }

    public void onAiSurveySuccess() {
        this.aiTopicField.setText("");
        this.cardLayout.show(this.mainContainer, this.VIEW_DASHBOARD);
    }

    private void handleManualSurvey() {
        // רשימה שתשמור את כל השאלות התקינות שהוזנו
        java.util.List<SurveyData> surveyQuestions = new java.util.ArrayList<>();

        // עוברים על 3 הבלוקים (בקפיצות של 5 שדות לכל בלוק)
        for (int block = 0; block < 3; block++) {
            int offset = block * 5; // האינדקס של השאלה הנוכחית (0, 5, או 10)
            String question = this.allManualFields.get(offset).getText().trim();

            // אם השאלה ריקה
            if (question.isEmpty()) {
                if (block == 0) {
                    // חובה למלא לפחות את השאלה הראשונה!
                    JOptionPane.showMessageDialog(this, "נא להזין לפחות את השאלה הראשונה", "שגיאה", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                continue; // שאלות 2 ו-3 הן אופציונליות, אז פשוט נדלג עליהן אם הן ריקות
            }

            // אם יש שאלה, אוספים את התשובות שלה (4 השדות הבאים)
            java.util.List<String> answers = new java.util.ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                String ans = this.allManualFields.get(offset + i).getText().trim();
                if (!ans.isEmpty()) {
                    answers.add(ans);
                }
            }

            // מוודאים שיש לפחות 2 תשובות לשאלה הספציפית הזו
            if (answers.size() < 2) {
                JOptionPane.showMessageDialog(this, "שאלה " + (block + 1) + " חייבת להכיל לפחות 2 תשובות!", "שגיאה", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // שומרים את השאלה התקינה ברשימה
            surveyQuestions.add(new SurveyData(question, answers));
        }

        this.bot.setSurveyActive(true); // נועלים את המערכת לסקר חדש

        for (SurveyData poll : surveyQuestions) {
            this.bot.broadcastSurvey(poll);
        }

        JOptionPane.showMessageDialog(this, "הסקר הידני נשלח לטלגרם בהצלחה!", "הצלחה", JOptionPane.INFORMATION_MESSAGE);

        // מנקים את כל 15 השדות
        for (JTextField field : this.allManualFields) {
            field.setText("");
        }
        this.cardLayout.show(this.mainContainer, this.VIEW_DASHBOARD);
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
            label.setPreferredSize(new Dimension(140, 20));
            rowPanel.add(label, BorderLayout.LINE_START);

            JTextField textField = new JTextField(30);
            this.allManualFields.add(textField);
            textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 20));
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

    public void broadcastSurvey(SurveyData survey) {
        for (CommunityUser user : this.communityUsers.values()) {
            sendSurveyToChat(String.valueOf(user.getChatId()), survey);
        }
    }
}