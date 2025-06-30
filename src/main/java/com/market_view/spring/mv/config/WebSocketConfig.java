package com.market_view.spring.mv.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Configure the message broker to handle messages
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Set the prefix for messages destined for the application itself
        config.setApplicationDestinationPrefixes("/app");

        // Enable a simple in-memory message broker with a specified prefix
        // Clients can subscribe to topics that start with "/topic"
        config.enableSimpleBroker("/topic");
    }

    // Register the Stomp endpoints, which clients can connect to
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Configure the endpoint for WebSocket communication
        // You can access the WebSocket at "/ws" from the client side
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");

        // Enable SockJS fallback for browsers that do not support WebSocket directly
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
