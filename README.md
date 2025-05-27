# 🎤 실시간 음성 대화형 챗봇 (Java Spring Boot + OpenAI)

사용자의 **실시간 음성 입력**을 받아,  
ChatGPT(GPT-4o)를 통해 답변을 생성하고,  
**선택한 말투로 음성으로 응답하는** 대화형 챗봇 시스템입니다.

> 마이크로 직접 말하면, GPT가 듣고, 말투에 맞춰 말해줍니다.

---

## 🚀 시스템 흐름

1. 브라우저에서 녹음 버튼 클릭
2. 사용자가 마이크로 질문
3. 녹음이 끝나면 음성(webm)이 서버로 전송됨
4. 서버는 아래 순서로 처리:
   - 🎙️ Whisper API → 텍스트 변환
   - 🧠 GPT-4o API → 텍스트 응답 생성
   - 🔊 TTS API → 선택한 말투로 음성 생성
5. 생성된 음성(wav)을 브라우저에서 자동 재생

---

## 🧩 사용 API

| 단계 | API | 설명 |
|------|-----|------|
| STT | `OpenAI Whisper API` | 음성(webm) → 텍스트 변환 |
| GPT | `OpenAI Chat API` (gpt-4o) | 텍스트 → 응답 생성 |
| TTS | `OpenAI TTS API` (tts-1) | 응답 텍스트 → 말투 기반 음성 생성 |

---

## 🛠 기술 스택

### 프론트엔드 (HTML + JS)
- `MediaRecorder API`: 실시간 마이크 녹음

### 백엔드 (Java + Spring Boot)
- `/api/stt` POST 엔드포인트
- `WhisperService`: 음성 → 텍스트
- `GptService`: 텍스트 → GPT 응답
- `TtsService`: 텍스트 → 음성 생성
- `VoiceChatController`: 전체 처리 흐름 통합

---

## 시스템 흐름 구성도
![213856이지훈 대화형chatbot 과제 drawio (2)](https://github.com/user-attachments/assets/d17e62b3-8f50-40d7-9c93-601bd79cda03)

---

## 사용자 입력 (음성)
![버전2-1](https://github.com/user-attachments/assets/89649510-78d9-4975-bf51-73e18449ec47)

---

## 🔐 API Key 보안

- API 키는 `.env` 파일에 저장
- `dotenv-java` 라이브러리로 키 불러옴
- `.gitignore`에 `.env` 포함 → GitHub 노출 방지

```env
# .env
OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxx

