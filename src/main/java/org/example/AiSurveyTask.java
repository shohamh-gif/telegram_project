package org.example;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import com.google.gson.Gson;

public class AiSurveyTask extends SwingWorker<String, Void> {
    private String topic;
    private SurveyApiService apiService;
    private MyBot bot;
    private DashboardFrame dashboard;
    private JButton sendBtn;

    // בנאי שמקבל את כל התלויות מבחוץ
    public AiSurveyTask(String topic, SurveyApiService apiService, MyBot bot, DashboardFrame dashboard, JButton sendBtn) {
        this.topic = topic;
        this.apiService = apiService;
        this.bot = bot;
        this.dashboard = dashboard;
        this.sendBtn = sendBtn;
    }

    @Override
    protected String doInBackground() {
        // רץ ברקע מול השרת
        return this.apiService.generateSurvey(this.topic);
    }

    @Override
    protected void done() {
        try {
            String result = get();
            if (result != null) {
                result = result.replace("```json", "").replace("```", "").trim();
                Gson gson = new Gson();
                SurveyData survey = gson.fromJson(result, SurveyData.class);

                this.bot.broadcastSurvey(survey);

                JOptionPane.showMessageDialog(this.dashboard, "הסקר נשלח לטלגרם בהצלחה!", "הצלחה", JOptionPane.INFORMATION_MESSAGE);
                this.dashboard.onAiSurveySuccess(); // קריאה למסך שיתעדכן
            } else {
                this.bot.setSurveyActive(false);
                JOptionPane.showMessageDialog(this.dashboard, "שגיאה ביצירת הסקר מול השרת.", "שגיאה", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            this.bot.setSurveyActive(false);
            JOptionPane.showMessageDialog(this.dashboard, "שגיאה בפירוק הנתונים או בשליחה.", "שגיאה", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            // החזרת הכפתור למצב פעיל
            this.sendBtn.setEnabled(true);
            this.sendBtn.setText("שלח סקר");
        }
    }
}