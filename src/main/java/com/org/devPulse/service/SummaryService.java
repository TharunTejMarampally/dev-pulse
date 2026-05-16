package com.org.devPulse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.org.devPulse.utils.DevPulseConstants.HF_URL;

public class SummaryService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${hugging-face.token}")
    private String hfToken;

    public String summarize(String articleText) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(hfToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HF has input size limits → trim text
        String input = articleText.length() > 3000
                ? articleText.substring(0, 3000)
                : articleText;

        String body = """
                {
                  "inputs": "%s"
                }
                """.formatted(input.replace("\"", "'"));

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(HF_URL, entity, String.class);

        return extractSummary(response.getBody());
    }

    private String extractSummary(String json) {
        // HF returns: [{"summary_text":"..."}]
        int start = json.indexOf("summary_text") + 15;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
