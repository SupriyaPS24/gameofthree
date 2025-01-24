package com.jet.gameofthree.domain.event;

import com.jet.gameofthree.domain.Player;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class EventPublisher {

	private final ApplicationEventPublisher publisher;

	public void publishUserRegisteredEvent(final Player player) {
		publisher.publishEvent(new PlayerRegisteredEvent(player));
	}

	public void publishGameStartedEvent(final UUID gameIdentifier) {
		publisher.publishEvent(new GameStartedEvent(gameIdentifier));
	}

}
