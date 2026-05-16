package com.org.devPulse.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.org.devPulse.utils.DevPulseConstants.GEMENI_URL;

@Service
@Slf4j
public class SummaryService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gemini.token}")
    private String apiKey;


    public String summarize(String articleText) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String input = articleText.length() > 3000
                ? articleText.substring(0, 3000)
                : articleText;

        Map<String, Object> part = new HashMap<>();
        part.put("text", input);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(GEMENI_URL + "?key=" + apiKey, entity, String.class);

        return extractSummary(response.getBody());
    }

    private String extractSummary(String json) {
        try {
            JsonNode root = mapper.readTree(json);

            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            log.error("Gemini parse error", e);
            throw new RuntimeException("Invalid Gemini JSON: " + json, e);
        }
    }
}