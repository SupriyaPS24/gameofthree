package com.jet.gameofthree.domain;

import com.jet.gameofthree.domain.enums.PlayerStatusEnum;
import com.jet.gameofthree.domain.event.EventPublisher;
import com.jet.gameofthree.domain.model.Move;
import com.jet.gameofthree.domain.model.PlayerRole;
import com.jet.gameofthree.dto.MoveResponse;
import com.jet.gameofthree.dto.StartResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class Player {
    private String name;
    private String playerFullName;
    private PlayerRole role;
    private Move lastMove;
    private boolean automaticPlayer;

    public Player(final String name, final String playerFullName, final boolean automaticPlayer) {
        this.name = name;
        this.playerFullName = playerFullName;
        this.automaticPlayer = automaticPlayer;
    }

    public boolean isPlayerOne() {
        return PlayerRole.PLAYER1.equals(this.role);
    }

    public void announcePlayerRegisteredToGame(final EventPublisher eventPublisher) {
        eventPublisher.publishUserRegisteredEvent(this);
    }

    public void announcePlayerMoved(final SimpMessagingTemplate messagingTemplate, final PlayerStatusEnum playerStatusEnum,
                                    final String message) {
        MoveResponse gameStartedResponse = new MoveResponse(playerStatusEnum.toString(), message);
        messagingTemplate.convertAndSendToUser(this.getPlayerFullName(), "/queue/playerEvents", gameStartedResponse);
    }

    public void announcePlayerWon(final SimpMessagingTemplate messagingTemplate, final Player winner) {
        String message = String.format("%s won", winner.getName());
        messagingTemplate.convertAndSendToUser(this.getPlayerFullName(), "/queue/playerEvents",
                new MoveResponse(PlayerStatusEnum.PLAYER_WON.toString(), message));
        messagingTemplate.convertAndSend("/topic/gameEvents", message);
        log.info("{} won", winner.getName());
    }

    public void announcePlayerJoined(final SimpMessagingTemplate messagingTemplate) {
        messagingTemplate.convertAndSendToUser(this.getPlayerFullName(), "/queue/playerEvents", new StartResponse(
                this.getName(), PlayerStatusEnum.PLAYER2_TURN.toString(), this.getRole().getWelcomeText()));
        log.info("Welcome to Game of Three: {}", this.getName());
    }

    public String generatePlayerMessage() {
        return String.format(" %s added number: %s, and resulting number after division: %s", this.getName(),
                this.getLastMove().getMoveNumber(), this.getLastMove().getAfterMoveNumber());
    }
}
