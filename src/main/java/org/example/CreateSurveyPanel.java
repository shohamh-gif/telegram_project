package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CreateSurveyPanel extends JPanel {
    private DashboardFrame parentFrame;
    private SurveyApiService apiService;
    private JTextField aiTopicField;
    private List<JTextField> allManualFields;
    private JTextField delayField;
    private JLabel countdownLabel;

    public CreateSurveyPanel(DashboardFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.apiService = new SurveyApiService();
        this.allManualFields = new ArrayList<>();

        this.setLayout(new BorderLayout(5, 5));
        this.setBackground(DashboardFrame.MAIN_BG_COLOR);
        this.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JPanel cardPanel = new JPanel(new BorderLayout(5, 5));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DashboardFrame.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        JLabel titleLabel = new JLabel("הגדרות סקר חדש", SwingConstants.CENTER);
        titleLabel.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 20));
        titleLabel.setForeground(DashboardFrame.ACCENT_COLOR);
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
        this.add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createDelayAndSendPanel() {
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        delayPanel.setBackground(Color.WHITE);
        delayPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel label = new JLabel("השהיה לפני שליחה (בדקות, 0 למיידי):");
        label.setFont(new Font(DashboardFrame.FONT_NAME, Font.PLAIN, 14));
        delayPanel.add(label);

        this.delayField = new JTextField("0", 4);
        this.delayField.setHorizontalAlignment(JTextField.CENTER);
        this.delayField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DashboardFrame.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        delayPanel.add(this.delayField);

        JButton sendBtn = this.createStyledButton("שלח סקר");
        sendBtn.setPreferredSize(new Dimension(130, 32));

        sendBtn.addActionListener(e -> {
            MyBot bot = this.parentFrame.getBot();
            if (bot == null) return;

            if (bot.getCommunityUsers().size() < 3) {
                StyledDialogs.showWarning(this.parentFrame, "לא ניתן להתחיל סקר. נדרשים לפחות 3 חברים בקהילה!");
                return;
            }
            if (bot.isSurveyActive()) {
                StyledDialogs.showWarning(this.parentFrame, "יש סקר פעיל (או בהשהיה) כרגע! חובה להמתין לסיומו.");
                return;
            }

            int delayMinutes;
            try {
                delayMinutes = Integer.parseInt(this.delayField.getText().trim());
            } catch (NumberFormatException ex) {
                StyledDialogs.showError(this.parentFrame, "נא להזין מספר דקות תקין!");
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
        this.countdownLabel.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 15));
        this.countdownLabel.setForeground(DashboardFrame.ACCENT_COLOR);
        delayPanel.add(this.countdownLabel);

        return delayPanel;
    }

    private JPanel createRadioPanel(CardLayout inputCardLayout, JPanel inputContainer) {
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JRadioButton manualRadio = new JRadioButton("יצירה ידנית");
        JRadioButton aiRadio = new JRadioButton("ChatGPT (אוטומטי)");

        manualRadio.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 14));
        aiRadio.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 14));
        manualRadio.setBackground(Color.WHITE);
        aiRadio.setBackground(Color.WHITE);
        manualRadio.setForeground(DashboardFrame.DARK_TEXT);
        aiRadio.setForeground(DashboardFrame.DARK_TEXT);
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
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(this.createQuestionBlock(2));
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
        label.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 14));
        aiPanel.add(label);

        this.aiTopicField = new JTextField(30);
        this.aiTopicField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        this.aiTopicField.setHorizontalAlignment(JTextField.RIGHT);

        this.aiTopicField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DashboardFrame.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        this.aiTopicField.setFont(new Font(DashboardFrame.FONT_NAME, Font.PLAIN, 14));
        aiPanel.add(this.aiTopicField);
        return aiPanel;
    }

    private void executeSurveyDispatch(JButton sendBtn) {
        if (this.aiTopicField.isShowing()) {
            handleAISurvey(sendBtn);
        } else {
            handleManualSurvey();
        }
    }

    private void startCountdown(int minutes, JButton sendBtn) {
        this.parentFrame.getBot().setSurveyActive(true);
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
                this.countdownLabel.setText("מעבד נתונים...");
                executeSurveyDispatch(sendBtn);
            }
        });
        timer.start();
    }

    public void resetCountdownLabel() {
        this.countdownLabel.setText("");
    }

    private void handleAISurvey(JButton sendBtn) {
        String topic = this.aiTopicField.getText().trim();
        if (topic.isEmpty()) {
            StyledDialogs.showWarning(this.parentFrame, "נא להזין נושא לסקר");
            return;
        }

        this.parentFrame.getBot().setSurveyActive(true);
        sendBtn.setEnabled(false);
        sendBtn.setText("מייצר סקר...");

        // כאן אנחנו שולחים גם את הפאנל הנוכחי כדי שנוכל לעדכן אותו
        new AiSurveyTask(topic, this.apiService, this.parentFrame.getBot(), this.parentFrame, this, sendBtn).execute();
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
                    StyledDialogs.showWarning(this.parentFrame, "נא להזין לפחות את השאלה הראשונה");
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
                StyledDialogs.showWarning(this.parentFrame, "שאלה " + (block + 1) + " חייבת להכיל לפחות 2 תשובות!");
                return;
            }

            surveyQuestions.add(new SurveyData(question, answers));
        }

        this.parentFrame.getBot().startSurvey(surveyQuestions);

        StyledDialogs.showSuccess(this.parentFrame, "הסקר הידני נשלח לטלגרם בהצלחה!");

        for (JTextField field : this.allManualFields) {
            field.setText("");
        }
        this.countdownLabel.setText("");
    }

    private JPanel createQuestionBlock(int qNum) {
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
                label.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 14));
                label.setForeground(DashboardFrame.ACCENT_COLOR);
            } else {
                label.setFont(new Font(DashboardFrame.FONT_NAME, Font.PLAIN, 14));
                label.setForeground(DashboardFrame.DARK_TEXT);
            }
            label.setPreferredSize(new Dimension(140, 20));
            rowPanel.add(label, BorderLayout.LINE_START);

            JTextField textField = new JTextField();
            textField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            textField.setHorizontalAlignment(JTextField.RIGHT);

            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DashboardFrame.BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)
            ));
            textField.setFont(new Font(DashboardFrame.FONT_NAME, Font.PLAIN, 14));

            this.allManualFields.add(textField);
            rowPanel.add(textField, BorderLayout.CENTER);

            panel.add(rowPanel);
        }

        if (qNum < 3) {
            panel.setBorder(BorderFactory.createCompoundBorder(
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
        btn.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 14));
        btn.setBackground(DashboardFrame.BUTTON_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(170, 40));
        btn.setFocusPainted(false);
        return btn;
    }
}