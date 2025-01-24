package com.jet.gameofthree.domain.model;

import com.jet.gameofthree.domain.Game;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Component
public class GameRoom {

    private ConcurrentHashMap<UUID, Game> games = new ConcurrentHashMap<UUID, Game>();
    private ConcurrentHashMap<String, UUID> players = new ConcurrentHashMap<String, UUID>();
}
