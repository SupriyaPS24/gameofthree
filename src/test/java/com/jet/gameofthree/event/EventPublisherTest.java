package com.jet.gameofthree.event;

import com.jet.gameofthree.domain.Player;
import com.jet.gameofthree.domain.event.EventPublisher;
import com.jet.gameofthree.domain.event.GameStartedEvent;
import com.jet.gameofthree.domain.event.PlayerRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class EventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventPublisher = new EventPublisher(applicationEventPublisher);
    }

    @Test
    void testPublishUserRegisteredEvent() {
        Player player = new Player("Player1", "John", false);
        eventPublisher.publishUserRegisteredEvent(player);

        verify(applicationEventPublisher).publishEvent(any(PlayerRegisteredEvent.class));
    }

    @Test
    void testPublishGameStartedEvent() {
        UUID gameId = UUID.randomUUID();
        eventPublisher.publishGameStartedEvent(gameId);

        verify(applicationEventPublisher).publishEvent(any(GameStartedEvent.class));
    }

}
