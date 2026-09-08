package org.example;

import okhttp3.*;
import java.io.IOException;

public class SurveyApiService {

    private final String BASE_URL = "https://shaitest-production-3066.up.railway.app";
    private final OkHttpClient client;

    public SurveyApiService() {
        this.client = new OkHttpClient();
    }

    public String generateSurvey(String topic) {
        String jsonPrompt = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"צור סקר קצר בנושא " + topic + " עם שאלה אחת ו-2 עד 4 תשובות אפשריות. החזר את התשובה בפורמט JSON בלבד.\"}]"
                + "}";

        RequestBody body = RequestBody.create(
                jsonPrompt,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(this.BASE_URL + "/chat/completions")
                .post(body)
                .build();

        try (Response response = this.client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                System.out.println("שגיאה מקבלת נתונים מהשרת: " + response.code());
                return null;
            }
        } catch (IOException e) {
            System.out.println("שגיאת תקשורת עם השרת: " + e.getMessage());
            return null;
        }
    }
}