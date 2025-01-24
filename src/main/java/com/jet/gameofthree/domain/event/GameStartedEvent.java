package com.jet.gameofthree.domain.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class GameStartedEvent extends ApplicationEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	private UUID gameKey;

	GameStartedEvent(UUID gameKey) {
		super(GameStartedEvent.class);
		this.gameKey = gameKey;
	}

}