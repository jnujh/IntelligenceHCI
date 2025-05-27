package com.chatbot.voicebot.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class WhisperService {

    private final String apiKey;

    public WhisperService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("OPENAI_API_KEY");
    }

    public String transcribe(MultipartFile audioFile) throws IOException {
        // 1. MultipartFile → File로 저장
        File tempFile = convertMultipartFileToFile(audioFile);

        // 2. Whisper API 호출
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse(audioFile.getContentType());  // webm 등 자동 처리

        RequestBody fileBody = RequestBody.create(tempFile, mediaType);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", tempFile.getName(), fileBody)
                .addFormDataPart("model", "whisper-1")
                .build();

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Whisper API 호출 실패: " + response.code() + " - " + response.message() + "\n" + body);
            }
            JSONObject json = new JSONObject(body);
            return json.getString("text");
        } finally {
            tempFile.delete();  // 임시 파일 삭제
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipart) throws IOException {
        File file = File.createTempFile("recorded-", ".webm");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipart.getBytes());
        }
        return file;
    }
}
