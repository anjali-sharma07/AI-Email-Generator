package com.email.generator.service;

import com.email.generator.entity.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailGenerateService {

    @Value("${cohere.api.key}")
    private String cohereApiKey;

    private final WebClient webClient;

    public EmailGenerateService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getEmail(EmailRequest request) {
      // build prompt
        String prompt = buildPrompt(request);
      // craft request
        Map<String, Object> requestBody = Map.of(
                "model", "command",
                "prompt", prompt,
                "max_tokens", 100,
                "temperature", 0.7,
                "k", 0,
                "p", 0.75,
                "stop_sequences", List.of(),
                "return_likelihoods", "NONE"
        );

        // do request and response
        String response = webClient.post()
                .uri("https://api.cohere.ai/v1/generate")
                .header("Authorization", "Bearer " + cohereApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // extract response and return
         return extractResponse(response);
    }
    private String extractResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Cohere returns: { "generations": [ { "text": "..." } ] }
            return root.path("generations").get(0).path("text").asText();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
/*
    private String buildPrompt(EmailRequest request){
        StringBuilder prompt = new StringBuilder();
        prompt.append("Write a professional email answer according to the email I get.");
        if(request.getTone() != null && !request.getTone().isEmpty()){
            prompt.append("According to the following tone, generate an email")
                    .append(request.getTone())
                    .append("tone");
        }   prompt.append("\n Original Email Request: \n")
                    .append(request.getEmailContent());
        return prompt.toString();
    }*/
private String buildPrompt(EmailRequest request) {
    StringBuilder prompt = new StringBuilder();

    prompt.append("You received the following email:\n\n");
    prompt.append("\"").append(request.getEmailContent()).append("\"\n\n");

    prompt.append("Write a professional");

    if (request.getTone() != null && !request.getTone().isEmpty()) {
        prompt.append(" and ").append(request.getTone());
    }

    prompt.append(" reply to this email.");

    return prompt.toString();
}

}
