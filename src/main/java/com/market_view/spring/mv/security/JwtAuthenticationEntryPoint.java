package com.market_view.spring.mv.security;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        HttpStatus status = HttpStatus.UNAUTHORIZED; // Default status
        String message = getString(authException);

        // Log the error
        logger.error("Authentication error: {}", message);


        // Create an error response in the same format as CustomException
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        OutputStream out = response.getOutputStream();
        objectMapper.writeValue(out, java.util.Map.of("message", message));
        out.flush();
    }

    private static String getString(AuthenticationException authException) {
        String message = "Authentication failed";

        // Determine a specific error type and message
        if (authException instanceof BadCredentialsException) {
            message = "Invalid credentials";
        } else if (authException instanceof InsufficientAuthenticationException) {
            message = "Missing authentication token";
        } else if (authException.getCause() != null) {
            message = authException.getCause().getMessage();
        } else if (authException.getMessage() != null) {
            message = authException.getMessage();
        }
        return message;
    }
}
