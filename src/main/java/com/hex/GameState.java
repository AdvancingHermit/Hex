package com.hex;

import lombok.Getter;
import lombok.Setter;

public class GameState {

    @Getter
    private int currentPlayer = 2;
    @Setter
    @Getter
    private int playerNum;
    @Setter
    @Getter
    private boolean gameFinished = false;
    @Setter
    private boolean swap = true;


    public GameState() {
    }

    public GameState(GameState other) {
        this.currentPlayer = other.currentPlayer;
        this.playerNum = other.playerNum;
        this.gameFinished = other.gameFinished;
        this.swap = other.swap;
    }

    public void nextPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }

    public boolean getSwap() {
        return swap;
    }

}
