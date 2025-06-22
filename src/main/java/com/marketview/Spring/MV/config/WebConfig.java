package com.marketview.Spring.MV.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward legacy endpoints to new structure
        registry.addViewController("/auth/register").setViewName("forward:/api/auth/legacy/register");
        registry.addViewController("/auth/login").setViewName("forward:/api/auth/legacy/login");
    }
}
