package com.market_view.spring.mv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @InjectMocks
    private AiService aiService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private final ObjectMapper actualMapper = new ObjectMapper(); // For escaping only

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiService, "openaiApiKey", "test-key");
        ReflectionTestUtils.setField(aiService, "openaiApiUrl", "https://api.openai.com/v1/chat/completions");
    }

    @Test
    void testSummarizeNewsSuccess() throws Exception {
        String input = "Market rises due to tech earnings.";
        String expectedResponse = "- Tech earnings boost market\n- Nasdaq jumps 2%\n- Apple leads gains";

        mockOpenAiResponse(expectedResponse);

        String result = aiService.summarizeNews(input);
        assertTrue(result.contains("Tech earnings") || result.contains("Apple"));
    }

    @Test
    void testAnswerQuestionSuccess() throws Exception {
        String input = "What caused the recent stock rally?";
        String expectedResponse = "Recent tech earnings drove the rally, with Nasdaq up 2%. Source: Bloomberg";

        mockOpenAiResponse(expectedResponse);

        String result = aiService.answerQuestion(input);
        assertTrue(result.contains("tech") || result.contains("Nasdaq"));
    }

    @Test
    void testCallOpenAiApiFailure() {
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("API Down"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            aiService.answerQuestion("test?");
        });

        assertTrue(exception.getMessage().contains("Failed to process AI request"));
    }

    private void mockOpenAiResponse(String aiContent) throws Exception {
        // Use real mapper to escape string properly
        String escapedContent = actualMapper.writeValueAsString(aiContent); // adds surrounding quotes and escapes \n

        String mockJson = """
            {
              "choices": [
                {
                  "message": {
                    "content": %s
                  }
                }
              ]
            }
            """.formatted(escapedContent);

        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
                .thenReturn(mockJson);

        JsonNode mockNode = actualMapper.readTree(mockJson);
        when(objectMapper.readTree(mockJson)).thenReturn(mockNode);
    }
}
