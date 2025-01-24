package com.jet.gameofthree.service;

import java.util.*;

import com.jet.gameofthree.domain.Game;
import com.jet.gameofthree.domain.model.GameRoom;
import com.jet.gameofthree.domain.Player;
import com.jet.gameofthree.domain.enums.PlayerStatusEnum;
import com.jet.gameofthree.domain.model.PlayerRole;
import com.jet.gameofthree.domain.event.PlayerRegisteredEvent;
import com.jet.gameofthree.dto.MoveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameService {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameRoom gameRoom;

    public GameService(SimpMessagingTemplate messagingTemplate, GameRoom gameRoom) {
        this.messagingTemplate = messagingTemplate;
        this.gameRoom = gameRoom;
    }

    @EventListener
    @Async
    public void registerPlayerToGame(PlayerRegisteredEvent event) {
        Optional<Game> findFirst = gameRoom.getGames().values().stream().filter(Game::isWaitingForPlayer).findFirst();
        if (findFirst.isEmpty()) {
            addNewPlayerToWaitingList(event.getPlayer());
            return;
        }
        addPlayerToWaitingGame(event, findFirst.get());
    }

    private void addPlayerToWaitingGame(PlayerRegisteredEvent event, Game game) {
        UUID keyGame = gameRoom.getGames().entrySet().stream().filter(entry -> game.equals(entry.getValue()))
                .findFirst().map(Map.Entry::getKey).orElse(null);
        Player player = event.getPlayer();
        player.setRole(PlayerRole.PLAYER2);
        game.getPlayers().add(player);
        gameRoom.getPlayers().put(player.getPlayerFullName(), keyGame);
        game.startTheGame(messagingTemplate);
        log.info("Players added to waiting game. Game started");
    }

    private void addNewPlayerToWaitingList(final Player player) {
        player.setRole(PlayerRole.PLAYER1);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player);
        UUID randomUUID = UUID.randomUUID();
        Game game = new Game(PlayerStatusEnum.PLAYER_UNAVAILABLE, playerList, 0, randomUUID);
        gameRoom.getGames().put(randomUUID, game);
        gameRoom.getPlayers().put(player.getPlayerFullName(), randomUUID);
        messagingTemplate.convertAndSend("/topic/gameEvents",
                String.format("%s Welcome to Game of Three, wait for other player to connect...", player.getName()));
        log.info("{} Joined to Game of Three, wait for other player to connect...",
                player.getName());    }

    public void playTheGame(String playerFullName, int adjustment) {
        UUID gameId = gameRoom.getPlayers().get(playerFullName);
        Game game = gameRoom.getGames().get(gameId);
        if (game == null || PlayerStatusEnum.PLAYER_WON.equals(game.getPlayerStatus())) {
            return;
        }
        Player moveOwner = game.getPlayers().stream()
                .filter(p -> playerFullName.equals(p.getPlayerFullName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found in game"));
        MoveResponse moveResponse = new MoveResponse();
        moveResponse.setMessage("NEXT");
        game.makePlayerMove(moveOwner, adjustment, moveResponse.getMessage(), messagingTemplate);
    }
}
