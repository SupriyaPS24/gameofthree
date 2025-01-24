package com.jet.gameofthree.domain.event;

import com.jet.gameofthree.domain.Player;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

@Getter
@Setter
public class PlayerRegisteredEvent extends ApplicationEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	private Player player;

	PlayerRegisteredEvent(Player player) {
		super(PlayerRegisteredEvent.class);
		this.player = player;
	}
}