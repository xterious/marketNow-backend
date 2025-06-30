package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.service.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AiControllerTest {

    @Mock
    private AiService aiService;

    @InjectMocks
    private AiController aiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSummarizeNews() {
        String newsText = "Long news article text...";
        String expectedSummary = "Short summary.";

        when(aiService.summarizeNews(newsText)).thenReturn(expectedSummary);

        ResponseEntity<String> response = aiController.summarizeNews(newsText);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedSummary, response.getBody());

        verify(aiService).summarizeNews(newsText);
    }

    @Test
    void testAnswerQuestion() {
        String question = "What is AI?";
        String expectedAnswer = "AI stands for Artificial Intelligence.";

        when(aiService.answerQuestion(question)).thenReturn(expectedAnswer);

        ResponseEntity<String> response = aiController.answerQuestion(question);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedAnswer, response.getBody());

        verify(aiService).answerQuestion(question);
    }
}
