package com.market_view.spring.mv.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market_view.spring.mv.util.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper;

// In your JwtFilter class, add this check at the beginning of doFilterInternal method:

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                               FilterChain filterChain) throws ServletException, IOException {
    
    // Skip JWT processing for WebSocket related requests
    String requestPath = request.getRequestURI();
    logger.debug("JwtFilter: Checking URI: {}", requestPath);
    if (requestPath.startsWith("/ws")) {
        filterChain.doFilter(request, response);
        return;
    }
    
    // Skip auth for permitted endpoints
    String requestURI = request.getRequestURI();
    if (shouldSkipAuthCheck(requestURI)) {
        filterChain.doFilter(request, response);
        return;
    }

    try {
        final String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Only throw exception if not accessing public endpoints
            if (!isPublicEndpoint(requestURI)) {
                throw new CustomException("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }
        } else {
            jwtToken = authHeader.substring(7);

            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (ExpiredJwtException e) {
                throw new CustomException("JWT Token has expired", HttpStatus.UNAUTHORIZED);
            } catch (MalformedJwtException e) {
                throw new CustomException("Invalid JWT token format", HttpStatus.UNAUTHORIZED);
            } catch (SignatureException e) {
                throw new CustomException("Invalid JWT signature", HttpStatus.UNAUTHORIZED);
            } catch (UnsupportedJwtException e) {
                throw new CustomException("Unsupported JWT token", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                throw new CustomException("Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        throw new CustomException("Invalid JWT token", HttpStatus.UNAUTHORIZED);
                    }
                } catch (UsernameNotFoundException e) {
                    throw new CustomException("User not found: " + username, HttpStatus.UNAUTHORIZED);
                }
            }
        }

        filterChain.doFilter(request, response);
    } catch (CustomException e) {
        // Handle custom exceptions and return a proper JSON response
        response.setStatus(e.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}

private boolean shouldSkipAuthCheck(String uri) {
    return isPublicEndpoint(uri);
}

private boolean isPublicEndpoint(String uri) {
    return uri.startsWith("/api/auth/") || 
           uri.startsWith("/oauth2/") || 
           uri.startsWith("/public/") || 
           uri.equals("/") ||
           uri.contains("swagger") ||
           uri.contains("api-docs") ||
           uri.contains("openapi") ||
           uri.startsWith("/webjars/");
}
}