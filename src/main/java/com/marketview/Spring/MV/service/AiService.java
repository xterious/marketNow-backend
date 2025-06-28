

package com.marketview.Spring.MV.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    public AiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String summarizeNews(String newsText) {
        String prompt = """
        As a financial analyst for MarketNow, summarize this text in 3 bullet points:

        Text: %s

        Guidelines:
        - Keep each bullet under 15 words
                - Focus on numbers, companies, and market impacts
        - Use markdown formatting with dashes
        """  + newsText;
        return callOpenAiApi(prompt);
    }

    public String answerQuestion(String question) {
        String prompt = """
            You are a financial analyst for MarketNow.
            Provide concise, accurate responses based on available market data.
            
            User Question: %s
            
            Guidelines:
            - If asked for predictions, clarify they are estimates
            - Cite sources when possible
            - Keep responses under 3 sentences unless detailed analysis is requested
            """ + question;
        return callOpenAiApi(prompt);
    }

    private String callOpenAiApi(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4.1-nano");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("max_tokens", 150); // Limit response length

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);

        try {
            String response = restTemplate.postForObject(
                    openaiApiUrl,
                    new org.springframework.http.HttpEntity<>(requestBody, headers),
                    String.class
            );
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            logger.error("Error calling Open AI API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process AI request: " + e.getMessage());
        }
    }
}