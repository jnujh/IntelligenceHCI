package com.chatbot.voicebot.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GptService {

    private final String apiKey;

    public GptService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("OPENAI_API_KEY");
    }

    public String ask(String userText) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // role 지시 포함: 답변은 7초 이내로 짧게 응답
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system")
                .put("content", "답변은 7초 이내 분량으로 짧고 친절하게 말해줘."));
        messages.put(new JSONObject().put("role", "user")
                .put("content", userText));

        JSONObject json = new JSONObject();
        json.put("model", "gpt-4o");
        json.put("messages", messages);

        RequestBody body = RequestBody.create(json.toString(),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new Exception("GPT 응답 실패: " + response.code() + " - " + responseBody);
            }

            JSONObject result = new JSONObject(responseBody);
            return result.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}
