package org.example;

import okhttp3.*;
import org.json.JSONObject;

public class SurveyApiService {

    public String generateSurvey(String topic) {
        try {
            String token = Utils.getSecureToken("TOKEN");

            if (token == null) {
                System.out.println("טוקן לא נמצא.");
                return null;
            }

            String prompt = "צור לי סקר על " + topic + " בפורמט JSON עם מפתח 'question' ומערך של 4 תשובות בשם 'answers'. תחזיר רק את ה-JSON.";

            OkHttpClient client = new OkHttpClient();

            HttpUrl url = HttpUrl.parse("https://shaitest-production-3066.up.railway.app/api-request").newBuilder()
                    .addQueryParameter("token", token)
                    .addQueryParameter("text", prompt).build();

            Request req = new Request.Builder().url(url).build();

            // שולחים את הבקשה
            Response response = client.newCall(req).execute();
            String responseData = response.body().string();

            // מפרקים את התשובה כדי לחלץ ממנה נטו את תוכן הסקר
            JSONObject json = new JSONObject(responseData);

            if (json.has("value")) {
                return json.getString("value"); // מחזירים את התשובה למסך!
            } else {
                System.out.println("השרת החזיר תשובה ללא ערך (value): " + responseData);
            }

        } catch (Exception e) {
            System.out.println("שגיאה בתקשורת מול השרת: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}