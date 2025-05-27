package com.chatbot.voicebot.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TtsService {

    private final String apiKey;

    public TtsService() {
        Dotenv dotenv = Dotenv.load();  // ✅ .env 파일 읽기
        this.apiKey = dotenv.get("OPENAI_API_KEY");  // ✅ 변수 읽기
    }

    public byte[] speak(String text, String voiceStyle) throws Exception {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("model", "tts-1"); // 또는 "tts-1-hd"
        json.put("input", text);
        json.put("voice", voiceStyle); // nova, onyx 등
        json.put("response_format", "wav");

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/speech")
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("TTS API 호출 실패: " + response.message());
            }
            return response.body().bytes(); // 오디오 바이너리 데이터
        }
    }
}
