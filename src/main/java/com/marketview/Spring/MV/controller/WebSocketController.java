package com.marketview.Spring.MV.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }
    
    // Inner class for the message structure
    public static class ChatMessage {
        private String sender;
        private String content;
        
        public ChatMessage() {}
        
        public ChatMessage(String sender, String content) {
            this.sender = sender;
            this.content = content;
        }
        
        public String getSender() {
            return sender;
        }
        
        public void setSender(String sender) {
            this.sender = sender;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
}