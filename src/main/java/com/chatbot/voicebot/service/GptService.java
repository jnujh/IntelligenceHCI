package com.chatbot.voicebot.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GptService {

    private final String apiKey;

    public GptService() {
        Dotenv dotenv = Dotenv.load();  // ✅ .env 파일 읽기
        this.apiKey = dotenv.get("OPENAI_API_KEY");  // ✅ 변수 읽기
    }

    public String ask(String userInput) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // GPT 메시지 형식 구성
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", userInput);

        JSONArray messages = new JSONArray();
        messages.put(message);

        JSONObject json = new JSONObject();
        json.put("model", "gpt-4o"); // 최신 모델 gpt-4o 또는 gpt-3.5-turbo 가능
        json.put("messages", messages);

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("GPT API 호출 실패: " + response.message());
            }
            String responseBody = response.body().string();
            JSONObject result = new JSONObject(responseBody);
            return result.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}
