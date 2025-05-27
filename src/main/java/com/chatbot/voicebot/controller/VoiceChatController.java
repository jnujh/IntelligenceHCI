package com.chatbot.voicebot.controller;

import com.chatbot.voicebot.service.WhisperService;
import com.chatbot.voicebot.service.GptService;
import com.chatbot.voicebot.service.TtsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VoiceChatController {

    private final WhisperService whisperService;
    private final GptService gptService;
    private final TtsService ttsService;

    @PostMapping("/stt")
    public ResponseEntity<Resource> handleVoiceRequest(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("voice") String voice
    ) throws Exception {

        log.info("▶ 음성 파일 수신: {}, 선택 말투: {}", audioFile.getOriginalFilename(), voice);

        // 1. 음성 → 텍스트 (STT)
        String userText = whisperService.transcribe(audioFile);
        log.info("▶ 변환된 텍스트: {}", userText);

        // 2. 텍스트 → GPT 응답
        String responseText = gptService.ask(userText);
        log.info("▶ GPT 응답: {}", responseText);

        // 3. GPT 응답 → TTS 음성
        byte[] audioBytes = ttsService.speak(responseText, voice);
        log.info("▶ 음성 변환 완료 (bytes): {}", audioBytes.length);

        // 4. 오디오 파일 반환
        ByteArrayResource resource = new ByteArrayResource(audioBytes);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(resource);
    }
}
