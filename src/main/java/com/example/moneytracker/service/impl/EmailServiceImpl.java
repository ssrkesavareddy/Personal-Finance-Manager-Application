package com.example.moneytracker.service.impl;

import com.example.moneytracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${T_EMAIL}")
    private String fromEmail;

    private final RestTemplate restTemplate;

    @Override
    public void sendEmail(String to, String subject, String body) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", brevoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "sender", Map.of(
                        "name", "MoneyTracker",
                        "email", fromEmail
                ),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "htmlContent", "<h3>Activate Account</h3><p>" + body + "</p>"
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        String responseBody = response.getBody();
        System.out.println("BREVO RESPONSE: " + responseBody);

        if (!response.getStatusCode().is2xxSuccessful() ||
                (responseBody != null && responseBody.contains("error"))) {

            throw new RuntimeException("Brevo failed: " + responseBody);
        }

        System.out.println("Email sent to: " + to);
    }
}
