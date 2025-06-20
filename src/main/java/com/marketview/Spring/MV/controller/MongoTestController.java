package com.marketview.Spring.MV.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MongoTestController {

    @GetMapping("/test")
    public String testMongo() {
        return "MongoDB connected!";
    }
}

