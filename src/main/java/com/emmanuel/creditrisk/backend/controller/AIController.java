package com.emmanuel.creditrisk.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:4200")
public class AIController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // ✅ CONFIRMED BY ListModels
    private static final String MODEL_NAME = "models/gemini-2.5-flash";

    private final RestTemplate restTemplate;

    public AIController() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(20000);
        this.restTemplate = new RestTemplate(factory);
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, String> request
    ) {
        String userQuestion = request.getOrDefault("question", "").trim();
        String context = request.getOrDefault(
                "context",
                "No credit application data available."
        );

        if (userQuestion.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "response",
                    "Ask me anything about your credit application."
            ));
        }

        String prompt =
                "You are a friendly Credit Wizard AI assistant.\n" +
                        "You give practical, encouraging credit advice.\n" +
                        "Respond in bullet points when helpful.\n" +
                        "Keep responses under 200 words.\n\n" +
                        "Latest credit application:\n" + context + "\n\n" +
                        "User question:\n" + userQuestion;

        Map<String, Object> geminiRequest = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-Goog-Api-Key", geminiApiKey);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(geminiRequest, headers);

        String url =
                "https://generativelanguage.googleapis.com/v1beta/"
                        + MODEL_NAME
                        + ":generateContent";

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, entity, Map.class);

            Map<String, Object> body = response.getBody();

            if (body == null || !body.containsKey("candidates")) {
                return ResponseEntity.ok(Map.of(
                        "response",
                        "The AI did not return a response."
                ));
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) body.get("candidates");

            @SuppressWarnings("unchecked")
            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");

            String text = parts.get(0).get("text").toString().trim();

            return ResponseEntity.ok(Map.of(
                    "response",
                    text
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "response",
                            "AI service error: " + e.getMessage()
                    ));
        }
    }
}









