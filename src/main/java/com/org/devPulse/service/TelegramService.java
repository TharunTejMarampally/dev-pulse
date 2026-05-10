package com.org.devPulse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TelegramService {
    
    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.chat-id}")
    private String chatId;

    public void send(String message) {
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";

        RestTemplate rt = new RestTemplate();
        rt.postForObject(url,
                Map.of("chat_id", chatId, "text", message),
                String.class);
    }
}
