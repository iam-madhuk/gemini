package com.gemini.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String apiKey;

    // simple in-memory store: sessionId -> conversation history
    /* TODO : will do it with REDIS once platform code is ready and deployed*/
    private final Map<String, List<String>> memory = new HashMap<>();

    public GeminiService(@Value("${GEMINI_API_KEY}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models")
                .build();
    }

    public Mono<String> askGemini(String sessionId, String prompt) {
        // get previous conversation
        List<String> history = memory.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // build request body with history + new prompt
        List<Map<String, Object>> parts = new ArrayList<>();
        for (String h : history) {
            parts.add(Map.of("text", h));
        }
        parts.add(Map.of("text", prompt));

        Map<String, Object> body = Map.of(
                "contents", new Object[]{
                        Map.of("parts", parts)
                }
        );

        String url = "/gemini-1.5-flash:generateContent?key=" + apiKey;

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(resp -> {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) resp.get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        List<Map<String, Object>> p = (List<Map<String, Object>>) content.get("parts");
                        if (p != null && !p.isEmpty()) {
                            String reply = p.get(0).get("text").toString();

                            // store both user input and model reply in memory
                            history.add("User: " + prompt);
                            history.add("AI: " + reply);

                            return reply;
                        }
                    }
                    return "No response text found.";
                });
    }


}
