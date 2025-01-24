package com.jet.gameofthree.controller;

import com.jet.gameofthree.domain.Player;
import com.jet.gameofthree.domain.event.EventPublisher;
import com.jet.gameofthree.dto.MoveRequest;
import com.jet.gameofthree.dto.RegisterRequest;
import com.jet.gameofthree.service.GameService;
import com.jet.gameofthree.util.StompPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
public class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private EventPublisher eventPublisher;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameController = new GameController(gameService, eventPublisher);
    }

    @Test
    void testRegisterPlayerToGame() {
        RegisterRequest registerRequest = new RegisterRequest("Player1", false);
        StompPrincipal stompPrincipal = new StompPrincipal("123");
        Player mockPlayer = mock(Player.class);

        when(mockPlayer.getName()).thenReturn("Player1");
        Player result = gameController.registerPlayerToGame(registerRequest, stompPrincipal);

        verify(eventPublisher).publishUserRegisteredEvent(result);
    }

    @Test
    void testPlayTheGame() {
        MoveRequest moveRequest = new MoveRequest(3);
        StompPrincipal stompPrincipal = new StompPrincipal("123");

        gameController.playTheGame(moveRequest, stompPrincipal);
        verify(gameService).playTheGame("123", 3);
    }
}
