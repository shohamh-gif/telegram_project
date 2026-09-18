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

            String prompt = "צור לי סקר בעברית על הנושא: \"" + topic + "\". " +
                    "הסקר צריך לכלול בין שאלה אחת לשלוש שאלות, ולכל שאלה בין 2 ל-4 אפשרויות תשובה קצרות וברורות. " +
                    "החזר אך ורק מערך JSON תקין, ללא טקסט נוסף וללא markdown, בפורמט הבא בדיוק: " +
                    "[{\"question\":\"...\",\"answers\":[\"...\",\"...\"]}]";

            OkHttpClient client = new OkHttpClient();

            HttpUrl url = HttpUrl.parse("https://shaitest-production-3066.up.railway.app/api-request").newBuilder()
                    .addQueryParameter("token", token)
                    .addQueryParameter("text", prompt).build();

            Request req = new Request.Builder().url(url).build();

            Response response = client.newCall(req).execute();
            String responseData = response.body().string();

            JSONObject json = new JSONObject(responseData);

            if (json.has("value")) {
                return json.getString("value");
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