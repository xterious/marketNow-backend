package com.marketview.Spring.MV.controller;



import com.marketview.Spring.MV.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/summarize")
    public ResponseEntity<String> summarizeNews(@RequestBody String newsText) {
        String summary = aiService.summarizeNews(newsText);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/ask")
    public ResponseEntity<String> answerQuestion(@RequestBody String question) {
        String answer = aiService.answerQuestion(question);
        return ResponseEntity.ok(answer);
    }
}