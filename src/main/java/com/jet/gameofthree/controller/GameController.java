package com.jet.gameofthree.controller;

import com.jet.gameofthree.domain.Player;
import com.jet.gameofthree.domain.event.EventPublisher;
import com.jet.gameofthree.dto.MoveRequest;
import com.jet.gameofthree.dto.RegisterRequest;
import com.jet.gameofthree.service.GameService;
import com.jet.gameofthree.util.GameWebSocketHandler;
import com.jet.gameofthree.util.StompPrincipal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class GameController {

    private final GameService gameService;
    private final EventPublisher eventPublisher;

    @MessageMapping("/register")
    public Player registerPlayerToGame(@Payload RegisterRequest registerRequest, StompPrincipal stompPrincipal) {
        Player player = new Player(registerRequest.getName(), stompPrincipal.getName(), registerRequest.isAutomaticPlayer());
        player.announcePlayerRegisteredToGame(eventPublisher);
        return player;
    }

    @MessageMapping("/play")
    public void playTheGame(@Payload MoveRequest moveRequest, StompPrincipal stompPrincipal) {
        gameService.playTheGame(stompPrincipal.getName(), moveRequest.getNumber());
    }
}
