package com.gemini.controller;

import com.gemini.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/ask/{sessionId}")
    public Mono<ResponseEntity<String>> askGemini(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String prompt = request.get("prompt");
        return geminiService.askGemini(sessionId, prompt)
                .map(ResponseEntity::ok);
    }
}
