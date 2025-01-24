package com.jet.gameofthree.domain;

import com.jet.gameofthree.domain.enums.PlayerStatusEnum;
import com.jet.gameofthree.domain.model.Move;
import com.jet.gameofthree.dto.MoveResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class Game {
    private PlayerStatusEnum playerStatus;
    private List<Player> players;
    private int currentNumber;
    private UUID gameId;

    public boolean isWaitingForPlayer() {
        return this.playerStatus == PlayerStatusEnum.PLAYER_UNAVAILABLE;
    }

    public void setGameStatus(Player lastPlayed) {
        if (lastPlayed.isPlayerOne()) {
            this.setGameStatus(PlayerStatusEnum.PLAYER2_TURN);
        } else {
            this.setGameStatus(PlayerStatusEnum.PLAYER1_TURN);
        }
    }

    public void setGameStatus(PlayerStatusEnum gameStatusEnum) {
        this.playerStatus = gameStatusEnum;
    }

    public void startTheGame(final SimpMessagingTemplate messagingTemplate) {
        Optional<Player> playerOne = this.getPlayers().stream().filter(Player::isPlayerOne).findFirst();
        if (playerOne.isEmpty()) {
            throw new IllegalStateException("Player One must be present to start the game.");
        }
        MoveResponse moveResponse = new MoveResponse();
        moveResponse.setMessage("FIRST");
        this.setGameStatus(PlayerStatusEnum.PLAYER1_TURN);
        this.getPlayers().forEach(p -> p.announcePlayerJoined(messagingTemplate));
        this.currentNumber = generateRandomNumber(10, 500);
        makePlayerMove(playerOne.get(), this.currentNumber, moveResponse.getMessage(),messagingTemplate);
        messagingTemplate.convertAndSend("/topic/gameEvents", generateGameMessage());
        log.info(generateGameMessage());
    }

    public void makePlayerMove(Player player, int adjustment, String moveResponse, SimpMessagingTemplate messagingTemplate) {
        int newNumber = "FIRST".equals(moveResponse) ? currentNumber : (this.currentNumber + adjustment) / 3;
        player.setLastMove(new Move(newNumber, adjustment));
        setCurrentNumber(newNumber);

        String moveMessage = player.generatePlayerMessage();
        setGameStatus(player);
        log.info(player.generatePlayerMessage());
        messagingTemplate.convertAndSend("/topic/currentNumber", newNumber);

        this.getPlayers().forEach(p -> {
            p.announcePlayerMoved(messagingTemplate, this.getPlayerStatus(), moveMessage);
            if (this.currentNumber == 1) {
                this.setGameStatus(PlayerStatusEnum.PLAYER_WON);
                p.announcePlayerWon(messagingTemplate, player);
            }
        });
    }

    private int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private String generateGameMessage() {
        return String.format("%s joined the game and matched with %s", this.getPlayers().get(1).getName(),
                this.getPlayers().get(0).getName());
    }
}