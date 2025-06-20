package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Market;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MarketWebSocketController {

    /**
     * This method listens for messages sent by clients to /app/market.
     * It processes the incoming Market object and broadcasts it to all subscribers
     * listening on /topic/market-updates.
     */
    @MessageMapping("/market")
    @SendTo("/topic/market-updates")
    public Market sendMarketUpdate(Market market) {
        // TODO: Add any processing logic here if needed
        return market;
    }
}
