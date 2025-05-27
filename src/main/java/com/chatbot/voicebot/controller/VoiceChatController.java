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
    public ResponseEntity<Resource> handleVoice(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("voice") String voiceStyle
    ) throws Exception {

        log.info("▶ 음성 파일 수신: {}, 말투: {}", audioFile.getOriginalFilename(), voiceStyle);

        // 1. 음성 → 텍스트 변환 (STT)
        String userText = whisperService.transcribe(audioFile);
        log.info("🗣️ STT 결과: {}", userText);

        // 2. GPT 응답 생성
        String gptResponse = gptService.ask(userText);
        log.info("💬 GPT 응답: {}", gptResponse);

        // 3. TTS 음성 생성
        byte[] audioBytes = ttsService.speak(gptResponse, voiceStyle);
        log.info("🔊 TTS 음성 생성 완료 ({} bytes)", audioBytes.length);

        // 4. 음성 파일 반환
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(new ByteArrayResource(audioBytes));
    }
}
