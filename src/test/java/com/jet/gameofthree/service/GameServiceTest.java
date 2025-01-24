package com.jet.gameofthree.service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.UUID;
import com.jet.gameofthree.domain.Game;
import com.jet.gameofthree.domain.Player;
import com.jet.gameofthree.domain.enums.PlayerStatusEnum;
import com.jet.gameofthree.domain.event.PlayerRegisteredEvent;
import com.jet.gameofthree.domain.model.GameRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

@SpringBootTest
public class GameServiceTest {

    @Mock
    private GameRoom gameRoom;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private PlayerRegisteredEvent playerRegisteredEvent;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(messagingTemplate, gameRoom);
    }

    @Test
    void testAddPlayerToWaitingGame() {

        Player player = new Player("Player1", "Chanduuu", false);
        when(playerRegisteredEvent.getPlayer()).thenReturn(player);
        Game mockGame = mock(Game.class);
        UUID gameId = UUID.randomUUID();

        List<Player> playersList = new ArrayList<>();
        playersList.add(player);
        when(mockGame.getPlayers()).thenReturn(playersList);
        when(mockGame.isWaitingForPlayer()).thenReturn(true);
        when(gameRoom.getGames()).thenReturn(new ConcurrentHashMap<UUID, Game>() {{
            put(gameId, mockGame);
        }});
        ConcurrentHashMap<String, UUID> mockPlayersMap = mock(ConcurrentHashMap.class);
        when(gameRoom.getPlayers()).thenReturn(mockPlayersMap);

        gameService.registerPlayerToGame(playerRegisteredEvent);
        verify(gameRoom, times(2)).getGames();
        verify(mockGame).startTheGame(messagingTemplate);
        verify(mockGame).getPlayers();
        verify(mockPlayersMap).put(eq(player.getPlayerFullName()), eq(gameId));
    }

    @Test
    void testPlayTheGame() {
        Player player = new Player("Player1", "David", false);
        Game mockGame = mock(Game.class);

        UUID gameId = UUID.randomUUID();
        when(gameRoom.getPlayers()).thenReturn(new ConcurrentHashMap<String, UUID>() {{
            put(player.getPlayerFullName(), gameId);
        }});
        when(gameRoom.getGames()).thenReturn(new ConcurrentHashMap<UUID, Game>() {{
            put(gameId, mockGame);
        }});
        when(mockGame.getPlayerStatus()).thenReturn(PlayerStatusEnum.PLAYER1_TURN);
        when(mockGame.getPlayers()).thenReturn(List.of(player));

        gameService.playTheGame(player.getPlayerFullName(), 3);
        verify(mockGame).makePlayerMove(eq(player), eq(3), eq("NEXT"), eq(messagingTemplate));
    }

    @Test
    void testPlayTheGameWhenGameIsWon() {
        Player player = new Player("Player1", "Mark", false);
        Game mockGame = mock(Game.class);

        UUID gameId = UUID.randomUUID();
        when(gameRoom.getPlayers()).thenReturn(new ConcurrentHashMap<String, UUID>() {{
            put(player.getPlayerFullName(), gameId);
        }});
        when(gameRoom.getGames()).thenReturn(new ConcurrentHashMap<UUID, Game>() {{
            put(gameId, mockGame);
        }});
        when(mockGame.getPlayerStatus()).thenReturn(PlayerStatusEnum.PLAYER_WON);
        gameService.playTheGame(player.getPlayerFullName(), 3);
        verify(mockGame, never()).makePlayerMove(any(), anyInt(), anyString(), any()); // Ensure no move was made since the game is won
    }
}
