package org.example;

import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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

    private JButton tabCommunityBtn;
    private JButton tabCreateBtn;
    private JButton tabLiveBtn;
    private JButton tabResultsBtn;
    private String activeTab;

    private DefaultTableModel liveStatusTableModel;
    private JLabel liveParticipantsValue;
    private JLabel liveCompletedValue;
    private JLabel liveRemainingValue;
    private JLabel liveTimeLeftLabel;
    private JLabel liveTitleLabel;

    private JPanel resultsContentPanel;
    private List<ActiveSurveySession.QuestionResult> lastResults;

    @Setter
    private MyBot bot;

    private final String VIEW_DASHBOARD = "Dashboard";
    private final String VIEW_SURVEY = "Survey";
    private final String VIEW_LIVE_STATUS = "LiveStatus";
    private final String VIEW_RESULTS = "Results";
    private final String MEMBERS_PREFIX = "סה\"כ חברים בקהילה: ";
    private final String FONT_NAME = "Segoe UI";

    private final Color BACKGROUND_PINK = new Color(253, 245, 247);
    private final Color HEADER_PINK = new Color(250, 220, 228);
    private final Color BUTTON_PINK = new Color(248, 190, 205);
    private final Color DARK_TEXT = new Color(70, 65, 68);
    private final Color BANNER_ROSE = new Color(224, 118, 151);
    private final Color BANNER_ROSE_DARK = new Color(200, 95, 128);
    private final Color TAB_BAR_BG = Color.WHITE;
    private final Color TAB_TEXT_INACTIVE = new Color(150, 140, 143);
    private final Color TAB_UNDERLINE_ACTIVE = new Color(214, 64, 110);

    private final Color GREEN_DONE = new Color(76, 175, 100);
    private final Color GREEN_ROW_BG = new Color(226, 247, 231);
    private final Color ORANGE_PROGRESS = new Color(214, 140, 40);
    private final Color YELLOW_ROW_BG = new Color(255, 246, 212);
    private final Color GRAY_NOT_STARTED = new Color(160, 155, 158);
    private final Color BLUE_ACCENT = new Color(66, 140, 224);
    private final Color TRACK_GRAY = new Color(233, 230, 231);

    public DashboardFrame() {
        this.apiService = new SurveyApiService();
        this.allManualFields = new ArrayList<>();
        this.setTitle("Telegram Survey Bot - לוח בקרה");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(this.createHeaderAndTabs(), BorderLayout.NORTH);

        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(this.cardLayout);
        this.mainContainer.add(this.createDashboardPanel(), this.VIEW_DASHBOARD);
        this.mainContainer.add(this.createSurveyPanel(), this.VIEW_SURVEY);
        this.mainContainer.add(this.createLiveStatusPanel(), this.VIEW_LIVE_STATUS);
        this.mainContainer.add(this.createResultsPanel(), this.VIEW_RESULTS);
        root.add(this.mainContainer, BorderLayout.CENTER);

        this.setContentPane(root);
        this.switchTab(this.VIEW_DASHBOARD);
    }

    private JPanel createHeaderAndTabs() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(this.BANNER_ROSE);
        banner.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Telegram Survey Bot  —  לוח בקרה");
        title.setFont(new Font(this.FONT_NAME, Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel titleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleWrap.setOpaque(false);
        titleWrap.add(title);

        banner.add(titleWrap, BorderLayout.WEST);
        wrapper.add(banner);

        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setBackground(this.TAB_BAR_BG);
        tabBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, this.HEADER_PINK));

        this.tabCommunityBtn = this.createTabButton("קהילה");
        this.tabCreateBtn = this.createTabButton("יצירת סקר");
        this.tabLiveBtn = this.createTabButton("סקר פעיל");
        this.tabResultsBtn = this.createTabButton("תוצאות");

        this.tabCommunityBtn.addActionListener(e -> this.switchTab(this.VIEW_DASHBOARD));
        this.tabCreateBtn.addActionListener(e -> this.switchTab(this.VIEW_SURVEY));
        this.tabLiveBtn.addActionListener(e -> this.switchTab(this.VIEW_LIVE_STATUS));
        this.tabResultsBtn.addActionListener(e -> this.switchTab(this.VIEW_RESULTS));

        tabBar.add(this.tabCommunityBtn);
        tabBar.add(this.tabCreateBtn);
        tabBar.add(this.tabLiveBtn);
        tabBar.add(this.tabResultsBtn);

        wrapper.add(tabBar);
        return wrapper;
    }

    private JButton createTabButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(this.FONT_NAME, Font.BOLD, 14));
        btn.setForeground(this.TAB_TEXT_INACTIVE);
        btn.setBackground(this.TAB_BAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(14, 22, 11, 22),
                BorderFactory.createMatteBorder(0, 0, 3, 0, this.TAB_BAR_BG)
        ));
        return btn;
    }

    private void switchTab(String viewKey) {
        this.activeTab = viewKey;
        this.cardLayout.show(this.mainContainer, viewKey);
        this.styleTab(this.tabCommunityBtn, this.VIEW_DASHBOARD.equals(viewKey));
        this.styleTab(this.tabCreateBtn, this.VIEW_SURVEY.equals(viewKey));
        this.styleTab(this.tabLiveBtn, this.VIEW_LIVE_STATUS.equals(viewKey));
        this.styleTab(this.tabResultsBtn, this.VIEW_RESULTS.equals(viewKey));
    }

    private void styleTab(JButton tab, boolean active) {
        tab.setForeground(active ? this.BANNER_ROSE_DARK : this.TAB_TEXT_INACTIVE);
        Color underline = active ? this.TAB_UNDERLINE_ACTIVE : this.TAB_BAR_BG;
        tab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(14, 22, 11, 22),
                BorderFactory.createMatteBorder(0, 0, 3, 0, underline)
        ));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(48); // שורות גבוהות ומרווחות יותר
        table.setFont(new Font(this.FONT_NAME, Font.PLAIN, 15));
        table.setForeground(this.DARK_TEXT);
        table.setSelectionBackground(new Color(250, 240, 243)); // ורוד חיוור מאוד בבחירה
        table.setSelectionForeground(this.DARK_TEXT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false); // מעלים קווים אנכיים למראה מודרני
        table.setGridColor(new Color(240, 240, 240)); // אפור בהיר ועדין
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(this.FONT_NAME, Font.BOLD, 15));
        header.setBackground(Color.WHITE);
        header.setForeground(this.BANNER_ROSE_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, this.HEADER_PINK)); // קו תחתון מודגש
        header.setPreferredSize(new Dimension(100, 45));
        header.setReorderingAllowed(false);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(this.BACKGROUND_PINK);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // מעטפת "כרטיס" לבנה לכל תוכן הקהילה
        JPanel cardPanel = new JPanel(new BorderLayout(0, 15));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(this.HEADER_PINK, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        this.totalMembersLabel = new JLabel(this.MEMBERS_PREFIX + "0");
        this.totalMembersLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 18));
        this.totalMembersLabel.setForeground(this.DARK_TEXT);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE); // רקע לבן בתוך הכרטיס
        topPanel.add(this.totalMembersLabel, BorderLayout.EAST);

        cardPanel.add(topPanel, BorderLayout.NORTH);

        // יצירת הטבלה ללא הגבול הכפול (כי יש גבול לכרטיס)
        JScrollPane tableScroll = this.createTablePanel();
        tableScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.HEADER_PINK));
        cardPanel.add(tableScroll, BorderLayout.CENTER);

        panel.add(cardPanel, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"מועד הצטרפות", "Telegram Username", "שם מלא"};
        this.tableModel = new NonEditableTableModel(columnNames, 0);

        JTable usersTable = new JTable(this.tableModel);
        this.styleTable(usersTable); // הפעלת העיצוב החדש!

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < usersTable.getColumnCount(); i++) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // הורדנו את המסגרת המיושנת
        return scrollPane;
    }

    private JPanel createSurveyPanel() {
        // צמצום הרווחים החיצוניים כדי לתת לטופס עוד גובה להימתח אליו
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(this.BACKGROUND_PINK);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JPanel cardPanel = new JPanel(new BorderLayout(5, 5));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(this.HEADER_PINK, 1, true),
                BorderFactory.createEmptyBorder(10, 25, 10, 25) // שוליים עליונים/תחתונים קטנים יותר
        ));

        JLabel titleLabel = new JLabel("הגדרות סקר חדש", SwingConstants.CENTER);
        titleLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 20));
        titleLabel.setForeground(this.BANNER_ROSE_DARK);
        cardPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout(5, 5));
        formPanel.setBackground(Color.WHITE);

        CardLayout inputCardLayout = new CardLayout();
        JPanel inputContainer = new JPanel(inputCardLayout);
        inputContainer.setBackground(Color.WHITE);

        inputContainer.add(this.createManualPanel(), "Manual");
        inputContainer.add(this.createAIPanel(), "AI");

        formPanel.add(this.createRadioPanel(inputCardLayout, inputContainer), BorderLayout.NORTH);
        formPanel.add(inputContainer, BorderLayout.CENTER);

        formPanel.add(this.createDelayAndSendPanel(), BorderLayout.SOUTH);

        cardPanel.add(formPanel, BorderLayout.CENTER);
        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDelayAndSendPanel() {
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        delayPanel.setBackground(Color.WHITE);
        delayPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel label = new JLabel("השהיה לפני שליחה (בדקות, 0 למיידי):");
        label.setFont(new Font(this.FONT_NAME, Font.PLAIN, 14));
        delayPanel.add(label);

        this.delayField = new JTextField("0", 4);
        this.delayField.setHorizontalAlignment(JTextField.CENTER);
        this.delayField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(this.HEADER_PINK, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        delayPanel.add(this.delayField);

        // יצירת כפתור השליחה קומפקטי יותר באותה שורה
        JButton sendBtn = this.createStyledButton("שלח סקר");
        sendBtn.setPreferredSize(new Dimension(130, 32));

        sendBtn.addActionListener(e -> {
            if (this.bot.getCommunityUsers().size() < 3) {
                JOptionPane.showMessageDialog(this, "לא ניתן להתחיל סקר. נדרשים לפחות 3 חברים בקהילה!", "חסימה", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.bot.isSurveyActive()) {
                JOptionPane.showMessageDialog(this, "יש סקר פעיל (או בהשהיה) כרגע! חובה להמתין לסיומו.", "חסימה", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int delayMinutes;
            try {
                delayMinutes = Integer.parseInt(this.delayField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "נא להזין מספר דקות תקין!", "שגיאה", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (delayMinutes > 0) {
                startCountdown(delayMinutes, sendBtn);
            } else {
                executeSurveyDispatch(sendBtn);
            }
        });
        delayPanel.add(sendBtn);

        this.countdownLabel = new JLabel("");
        this.countdownLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 15));
        this.countdownLabel.setForeground(this.BANNER_ROSE_DARK);
        delayPanel.add(this.countdownLabel);

        return delayPanel;
    }

    private JPanel createRadioPanel(CardLayout inputCardLayout, JPanel inputContainer) {
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.setBackground(Color.WHITE); // רקע נקי
        radioPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JRadioButton manualRadio = new JRadioButton("יצירה ידנית");
        JRadioButton aiRadio = new JRadioButton("ChatGPT (אוטומטי)");

        manualRadio.setFont(new Font(this.FONT_NAME, Font.BOLD, 14));
        aiRadio.setFont(new Font(this.FONT_NAME, Font.BOLD, 14));
        manualRadio.setBackground(Color.WHITE);
        aiRadio.setBackground(Color.WHITE);
        manualRadio.setForeground(this.DARK_TEXT);
        aiRadio.setForeground(this.DARK_TEXT);
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

    private JPanel createManualPanel() {
        JPanel manualQuestionsContainer = new JPanel(new BorderLayout());
        manualQuestionsContainer.setBackground(Color.WHITE);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        listPanel.add(this.createQuestionBlock(1));
        // המרווח הקסום: דוחף את השאלה הבאה למטה בצורה מבוקרת
        listPanel.add(Box.createVerticalStrut(15));

        listPanel.add(this.createQuestionBlock(2));
        // המרווח הקסום 2
        listPanel.add(Box.createVerticalStrut(15));

        listPanel.add(this.createQuestionBlock(3));

        manualQuestionsContainer.add(listPanel, BorderLayout.NORTH);
        return manualQuestionsContainer;
    }

    private JPanel createAIPanel() {
        JPanel aiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aiPanel.setBackground(Color.WHITE);
        aiPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel label = new JLabel("הזן נושא לסקר:");
        label.setFont(new Font(this.FONT_NAME, Font.BOLD, 14));
        aiPanel.add(label);

        this.aiTopicField = new JTextField(30);

        // הגדרת עברית גם עבור יצירת סקר AI
        this.aiTopicField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        this.aiTopicField.setHorizontalAlignment(JTextField.RIGHT);

        this.aiTopicField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(this.HEADER_PINK, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        this.aiTopicField.setFont(new Font(this.FONT_NAME, Font.PLAIN, 14));
        aiPanel.add(this.aiTopicField);
        return aiPanel;
    }

    private JPanel createDelayPanel() {
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        delayPanel.setBackground(Color.WHITE);
        delayPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel label = new JLabel("השהיה לפני שליחה (בדקות, 0 למיידי):");
        label.setFont(new Font(this.FONT_NAME, Font.PLAIN, 14));
        delayPanel.add(label);

        this.delayField = new JTextField("0", 5);
        this.delayField.setHorizontalAlignment(JTextField.CENTER);
        this.delayField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(this.HEADER_PINK, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        delayPanel.add(this.delayField);

        this.countdownLabel = new JLabel("");
        this.countdownLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 16));
        this.countdownLabel.setForeground(this.BANNER_ROSE_DARK);
        delayPanel.add(this.countdownLabel);
        return delayPanel;
    }

    private void executeSurveyDispatch(JButton sendBtn) {
        if (this.aiTopicField.isShowing()) {
            handleAISurvey(sendBtn);
        } else {
            handleManualSurvey();
        }
    }

    private void startCountdown(int minutes, JButton sendBtn) {
        this.bot.setSurveyActive(true);
        sendBtn.setEnabled(false);

        int totalSeconds = minutes * 60;
        int[] timeLeft = {totalSeconds};

        Timer timer = new Timer(1000, null);
        timer.addActionListener(e -> {
            if (timeLeft[0] > 0) {
                int mins = timeLeft[0] / 60;
                int secs = timeLeft[0] % 60;
                this.countdownLabel.setText(String.format("הסקר יישלח בעוד: %02d:%02d", mins, secs));
                timeLeft[0]--;
            } else {
                ((Timer) e.getSource()).stop();
                this.countdownLabel.setText("✓ הסקר נשלח!");
                executeSurveyDispatch(sendBtn);
            }
        });
        timer.start();
    }

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
        this.countdownLabel.setText("");
    }

    private void handleManualSurvey() {
        List<SurveyData> surveyQuestions = new ArrayList<>();

        for (int block = 0; block < 3; block++) {
            int offset = block * 5;
            String question = this.allManualFields.get(offset).getText().trim();

            if (question.isEmpty()) {
                if (block == 0) {
                    JOptionPane.showMessageDialog(this, "נא להזין לפחות את השאלה הראשונה", "שגיאה", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                continue;
            }

            List<String> answers = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                String ans = this.allManualFields.get(offset + i).getText().trim();
                if (!ans.isEmpty()) {
                    answers.add(ans);
                }
            }

            if (answers.size() < 2) {
                JOptionPane.showMessageDialog(this, "שאלה " + (block + 1) + " חייבת להכיל לפחות 2 תשובות!", "שגיאה", JOptionPane.WARNING_MESSAGE);
                return;
            }

            surveyQuestions.add(new SurveyData(question, answers));
        }

        this.bot.startSurvey(surveyQuestions);

        JOptionPane.showMessageDialog(this, "הסקר הידני נשלח לטלגרם בהצלחה!", "הצלחה", JOptionPane.INFORMATION_MESSAGE);

        for (JTextField field : this.allManualFields) {
            field.setText("");
        }
        this.countdownLabel.setText("");
    }

    private JPanel createQuestionBlock(int qNum) {
        // הגדלנו טיפ-טיפה את הרווח בין השורות של אותה שאלה מ-2 ל-4 כדי שלא יהיה חנוק
        JPanel panel = new JPanel(new GridLayout(5, 1, 0, 4));
        panel.setBackground(Color.WHITE);
        panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String optionalText = (qNum > 1) ? " (אופציונלי):" : ":";
        String[] labels = {
                "שאלה " + qNum + optionalText,
                "תשובה 1:",
                "תשובה 2:",
                "תשובה 3 (אופציונלי):",
                "תשובה 4 (אופציונלי):"
        };

        for (int i = 0; i < labels.length; i++) {
            JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
            rowPanel.setBackground(Color.WHITE);
            rowPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

            JLabel label = new JLabel(labels[i]);
            if (i == 0) {
                label.setFont(new Font(this.FONT_NAME, Font.BOLD, 14));
                label.setForeground(this.BANNER_ROSE_DARK);
            } else {
                label.setFont(new Font(this.FONT_NAME, Font.PLAIN, 14));
                label.setForeground(this.DARK_TEXT);
            }
            label.setPreferredSize(new Dimension(140, 20));
            rowPanel.add(label, BorderLayout.LINE_START);

            JTextField textField = new JTextField();

            textField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            textField.setHorizontalAlignment(JTextField.RIGHT);

            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.HEADER_PINK, 1, true),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8) // קצת יותר בשר לשדה
            ));
            textField.setFont(new Font(this.FONT_NAME, Font.PLAIN, 14));

            this.allManualFields.add(textField);
            rowPanel.add(textField, BorderLayout.CENTER);

            panel.add(rowPanel);
        }

        if (qNum < 3) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                    // 10 פיקסלים של אוויר נקי מתחת לתשובה 4, ואז הקו המפריד
                    BorderFactory.createEmptyBorder(0, 0, 10, 0),
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235))
            ));
        } else {
            panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        }
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(this.FONT_NAME, Font.BOLD, 14));
        btn.setBackground(this.BUTTON_PINK);
        btn.setForeground(this.DARK_TEXT);
        btn.setPreferredSize(new Dimension(170, 40));
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

    private JPanel createLiveStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setBackground(this.BACKGROUND_PINK);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        this.liveTitleLabel = new JLabel("אין סקר פעיל כרגע", SwingConstants.CENTER);
        this.liveTitleLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 24));
        this.liveTitleLabel.setForeground(this.BANNER_ROSE_DARK);
        panel.add(this.liveTitleLabel, BorderLayout.NORTH);

        JPanel centerStack = new JPanel(new BorderLayout(14, 14));
        centerStack.setBackground(this.BACKGROUND_PINK);

        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsRow.setBackground(this.BACKGROUND_PINK);

        this.liveParticipantsValue = new JLabel("0", SwingConstants.CENTER);
        this.liveCompletedValue = new JLabel("0", SwingConstants.CENTER);
        this.liveRemainingValue = new JLabel("0", SwingConstants.CENTER);

        cardsRow.add(this.createStatCard(this.liveParticipantsValue, "סה\"כ משתתפים"));
        cardsRow.add(this.createStatCard(this.liveCompletedValue, "סיימו"));
        cardsRow.add(this.createStatCard(this.liveRemainingValue, "טרם סיימו"));

        this.liveTimeLeftLabel = new JLabel("זמן שנותר: 05:00", SwingConstants.CENTER);
        this.liveTimeLeftLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 18));
        this.liveTimeLeftLabel.setForeground(Color.WHITE);
        this.liveTimeLeftLabel.setOpaque(true);
        this.liveTimeLeftLabel.setBackground(this.BANNER_ROSE);
        this.liveTimeLeftLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.liveTimeLeftLabel.setVisible(false);

        centerStack.add(cardsRow, BorderLayout.NORTH);
        centerStack.add(this.liveTimeLeftLabel, BorderLayout.SOUTH);

        String[] columnNames = {"שם", "התקדמות", "סטטוס"};
        this.liveStatusTableModel = new NonEditableTableModel(columnNames, 0);
        JTable liveTable = new JTable(this.liveStatusTableModel);

        this.styleTable(liveTable); // הפעלת העיצוב החדש גם פה!

        // שימוש במחלקה החיצונית שיצרנו כדי לצבוע ולמרכז את השורות
        StatusRowRenderer statusRenderer = new StatusRowRenderer();
        for (int i = 0; i < liveTable.getColumnCount(); i++) {
            liveTable.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(liveTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // מסגרת נקייה

        centerStack.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerStack, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(JLabel valueLabel, String caption) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(this.HEADER_PINK, 1, true),
                BorderFactory.createEmptyBorder(16, 10, 16, 10)
        ));

        valueLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 30));
        valueLabel.setForeground(this.BANNER_ROSE_DARK);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel captionLabel = new JLabel(caption, SwingConstants.CENTER);
        captionLabel.setFont(new Font(this.FONT_NAME, Font.PLAIN, 14));
        captionLabel.setForeground(this.DARK_TEXT);
        captionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(valueLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(captionLabel);
        return card;
    }

    public void onSurveyStarted(ActiveSurveySession session) {
        SwingUtilities.invokeLater(() -> {
            this.liveTitleLabel.setText("● סקר פעיל");
            // מציגים את הבר ברגע שהסקר מתחיל
            this.liveTimeLeftLabel.setVisible(true);
            this.switchTab(this.VIEW_LIVE_STATUS);
        });
    }

    public void updateSurveyStatus(ActiveSurveySession.SurveyStatusSnapshot snapshot) {
        SwingUtilities.invokeLater(() -> {
            this.liveStatusTableModel.setRowCount(0);
            for (ActiveSurveySession.ParticipantProgress p : snapshot.progressList) {
                String statusText;
                switch (p.status) {
                    case COMPLETED:
                        statusText = "השלים";
                        break;
                    case IN_PROGRESS:
                        statusText = "בתהליך";
                        break;
                    default:
                        statusText = "טרם ענה";
                }
                Object[] row = {p.user.getFirstName(), p.answered + "/" + p.total, statusText};
                this.liveStatusTableModel.addRow(row);
            }

            this.liveParticipantsValue.setText(String.valueOf(snapshot.totalParticipants));
            this.liveCompletedValue.setText(String.valueOf(snapshot.completedCount));
            this.liveRemainingValue.setText(String.valueOf(snapshot.totalParticipants - snapshot.completedCount));

            long totalSeconds = snapshot.remainingMs / 1000;
            long mins = totalSeconds / 60;
            long secs = totalSeconds % 60;
            this.liveTimeLeftLabel.setText(String.format("זמן שנותר: %02d:%02d", mins, secs));
        });
    }

    private JPanel createResultsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(this.BACKGROUND_PINK);
        outer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        this.resultsContentPanel = new JPanel();
        this.resultsContentPanel.setLayout(new BoxLayout(this.resultsContentPanel, BoxLayout.Y_AXIS));
        this.resultsContentPanel.setBackground(this.BACKGROUND_PINK);

        JScrollPane scrollPane = new JScrollPane(this.resultsContentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        outer.add(scrollPane, BorderLayout.CENTER);

        this.renderEmptyResults();
        return outer;
    }

    private void renderEmptyResults() {
        this.resultsContentPanel.removeAll();
        JLabel emptyLabel = new JLabel("אין תוצאות להצגה עדיין - סקר טרם הסתיים", SwingConstants.CENTER);
        emptyLabel.setFont(new Font(this.FONT_NAME, Font.PLAIN, 16));
        emptyLabel.setForeground(this.TAB_TEXT_INACTIVE);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
        this.resultsContentPanel.add(emptyLabel);
        this.resultsContentPanel.revalidate();
        this.resultsContentPanel.repaint();
    }

    public void showSurveyResults(List<ActiveSurveySession.QuestionResult> results) {
        SwingUtilities.invokeLater(() -> {
            this.lastResults = results;
            this.liveTitleLabel.setText("אין סקר פעיל כרגע");

            // מסתירים את הבר ברגע שהסקר נגמר
            this.liveTimeLeftLabel.setVisible(false);

            this.resultsContentPanel.removeAll();

            JLabel header = new JLabel("✓ הסקר נסגר - הנה התוצאות", SwingConstants.CENTER);
            header.setFont(new Font(this.FONT_NAME, Font.BOLD, 22));
            header.setForeground(this.DARK_TEXT);
            header.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.resultsContentPanel.add(header);
            this.resultsContentPanel.add(Box.createVerticalStrut(18));

            int qNum = 1;
            for (ActiveSurveySession.QuestionResult qr : results) {
                JPanel qPanel = this.buildQuestionResultPanel(qNum, qr);
                qPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                this.resultsContentPanel.add(qPanel);
                this.resultsContentPanel.add(Box.createVerticalStrut(18));
                qNum++;
            }

            this.resultsContentPanel.revalidate();
            this.resultsContentPanel.repaint();
            this.switchTab(this.VIEW_RESULTS);
        });
    }

    private JPanel buildQuestionResultPanel(int qNum, ActiveSurveySession.QuestionResult qr) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(this.HEADER_PINK, 1, true),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        panel.setMaximumSize(new Dimension(760, 400));
        panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel qLabel = new JLabel("שאלה " + qNum + ": " + qr.question);
        qLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 17));
        qLabel.setForeground(this.DARK_TEXT);
        qLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(qLabel);
        panel.add(Box.createVerticalStrut(12));

        int rank = 0;
        for (ActiveSurveySession.AnswerResult ar : qr.answers) {
            Color fillColor;
            if (ar.votes == 0) {
                fillColor = this.GRAY_NOT_STARTED;
            } else if (rank == 0) {
                fillColor = this.GREEN_DONE;
            } else {
                fillColor = this.BLUE_ACCENT;
            }

            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setBackground(Color.WHITE);
            row.setAlignmentX(Component.RIGHT_ALIGNMENT);
            row.setMaximumSize(new Dimension(720, 34));
            row.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

            JLabel answerLabel = new JLabel(ar.text);
            answerLabel.setFont(new Font(this.FONT_NAME, Font.BOLD, 13));
            answerLabel.setForeground(this.DARK_TEXT);
            answerLabel.setPreferredSize(new Dimension(150, 26));

            String barText = String.format("%.0f%%  (%d)", ar.percentage, ar.votes);
            PercentBarPanel bar = new PercentBarPanel(ar.percentage, barText, fillColor, this.TRACK_GRAY, this.FONT_NAME);
            bar.setPreferredSize(new Dimension(400, 30));

            row.add(answerLabel, BorderLayout.EAST);
            row.add(bar, BorderLayout.CENTER);
            panel.add(row);
            panel.add(Box.createVerticalStrut(6));
            rank++;
        }

        return panel;
    }

    private static class PercentBarPanel extends JPanel {
        private final double percentage;
        private final String text;
        private final Color fillColor;
        private final Color trackColor;
        private final String fontName;

        PercentBarPanel(double percentage, String text, Color fillColor, Color trackColor, String fontName) {
            this.percentage = percentage;
            this.text = text;
            this.fillColor = fillColor;
            this.trackColor = trackColor;
            this.fontName = fontName;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = h;

            g2.setColor(this.trackColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

            int fillWidth = (int) Math.round(w * Math.min(100, Math.max(0, this.percentage)) / 100.0);
            if (fillWidth > 0) {
                g2.setColor(this.fillColor);
                int drawWidth = Math.max(fillWidth, h); // כדי שהפינות המעוגלות ייראו תקין גם באחוז נמוך
                drawWidth = Math.min(drawWidth, w);
                g2.fill(new RoundRectangle2D.Double(0, 0, drawWidth, h, arc, arc));
            }

            g2.setFont(new Font(this.fontName, Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(this.text);
            int textX = (w - textWidth) / 2;
            int textY = (h + fm.getAscent() - fm.getDescent()) / 2;

            boolean textOverFill = (w / 2) < fillWidth;
            g2.setColor(textOverFill ? Color.WHITE : new Color(90, 85, 88));
            g2.drawString(this.text, textX, textY);

            g2.dispose();
        }
    }
}