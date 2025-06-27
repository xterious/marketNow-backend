package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Greetings;
import com.marketview.Spring.MV.model.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;


@Controller
public class GreetingController {

    // we have added /app as prefix in configurations so we call "/app/hello "from client

    @MessageMapping("/hello")
    @SendTo("/topic/greeting")
    public Greetings greet(HelloMessage message)
    {
        return new Greetings("Hello,"+HtmlUtils.htmlEscape(message.getName()));
    }
}
