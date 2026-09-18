package org.example;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class AiSurveyTask extends SwingWorker<String, Void> {
    private String topic;
    private SurveyApiService apiService;
    private MyBot bot;
    private DashboardFrame dashboard;
    private JButton sendBtn;

    public AiSurveyTask(String topic, SurveyApiService apiService, MyBot bot, DashboardFrame dashboard, JButton sendBtn) {
        this.topic = topic;
        this.apiService = apiService;
        this.bot = bot;
        this.dashboard = dashboard;
        this.sendBtn = sendBtn;
    }

    @Override
    protected String doInBackground() {
        return this.apiService.generateSurvey(this.topic);
    }

    @Override
    protected void done() {
        try {
            String result = get();
            if (result != null) {
                result = result.replace("```json", "").replace("```", "").trim();
                Gson gson = new Gson();
                // ה-API מתבקש להחזיר מערך JSON של 1-3 שאלות: [{"question":"...","answers":["...","..."]}]
                SurveyData[] questions = gson.fromJson(result, SurveyData[].class);

                if (questions == null || questions.length == 0) {
                    throw new IllegalStateException("לא התקבלו שאלות מה-AI");
                }

                List<SurveyData> questionList = Arrays.asList(questions);
                this.bot.startSurvey(questionList);

                JOptionPane.showMessageDialog(this.dashboard, "הסקר נשלח לטלגרם בהצלחה!", "הצלחה", JOptionPane.INFORMATION_MESSAGE);
                this.dashboard.onAiSurveySuccess();
            } else {
                this.bot.setSurveyActive(false);
                JOptionPane.showMessageDialog(this.dashboard, "שגיאה ביצירת הסקר מול השרת.", "שגיאה", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            this.bot.setSurveyActive(false);
            JOptionPane.showMessageDialog(this.dashboard, "שגיאה בפירוק הנתונים או בשליחה.", "שגיאה", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            this.sendBtn.setEnabled(true);
            this.sendBtn.setText("שלח סקר");
        }
    }
}