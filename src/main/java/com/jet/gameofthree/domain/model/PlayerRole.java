package com.jet.gameofthree.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRole {
    private String roleName;
    private String welcomeText;

    public static final PlayerRole PLAYER1 = new PlayerRole("PLAYER1", "Welcome to Game Of Three");
    public static final PlayerRole PLAYER2 = new PlayerRole("PLAYER2", "You are now connected, enjoy the game");
}
