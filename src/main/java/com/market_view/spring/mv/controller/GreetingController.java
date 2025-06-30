package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.Greetings;
import com.market_view.spring.mv.model.HelloMessage;
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
