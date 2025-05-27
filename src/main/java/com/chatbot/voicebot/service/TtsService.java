package com.chatbot.voicebot.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TtsService {

    private final String apiKey;

    public TtsService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("OPENAI_API_KEY");
    }

    public byte[] speak(String text, String voiceStyle) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // OpenAI TTS API 설정
        String json = new okhttp3.internal.platform.Platform().getClass().getSimpleName(); // placeholder 제거 필요
        String requestJson = String.format("""
            {
              "model": "tts-1",
              "input": "%s",
              "voice": "%s",
              "response_format": "wav"
            }
            """, text.replace("\"", "\\\""), voiceStyle);

        RequestBody body = RequestBody.create(
                requestJson,
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
                throw new Exception("TTS 생성 실패: " + response.code() + " - " + response.body().string());
            }

            return response.body().bytes();
        }
    }
}
