package com.chatbot.voicebot.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
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
        Dotenv dotenv = Dotenv.load();  // ✅ .env 파일 읽기
        this.apiKey = dotenv.get("OPENAI_API_KEY");  // ✅ 변수 읽기
    }

    public String transcribe(MultipartFile audioFile) throws IOException {
        File tempFile = convertMultipartFileToFile(audioFile);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("audio/wav");

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
                throw new IOException("Whisper API 호출 실패: " + response.code() + " - " + response.message() + "\nBody: " + body);
            }
            log.info("Whisper 응답 본문: {}", body);
            JSONObject json = new JSONObject(body);
            return json.getString("text");
        } catch (Exception e) {
            log.error("STT 처리 실패", e);
            throw new IOException("Whisper API 호출 실패");
        } finally {
            tempFile.delete(); // 임시 파일 삭제
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipart) throws IOException {
        File convFile = File.createTempFile("upload_", ".wav");
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipart.getBytes());
        }
        return convFile;
    }
}
