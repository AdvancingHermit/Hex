package com.hex;

import lombok.Getter;
import lombok.Setter;
// Made by Oscar
public class GameState {

    @Getter
    private int currentPlayer = 1;
    @Setter
    @Getter
    private int playerNum;
    @Setter
    @Getter
    private boolean gameFinished = false;
    @Setter
    private boolean swap = true;
    @Getter
    private int boardPieces = 0;


    public GameState() {
    }

    public GameState(GameState other) {
        this.currentPlayer = other.currentPlayer;
        this.playerNum = other.playerNum;
        this.gameFinished = other.gameFinished;
        this.swap = other.swap;
        this.boardPieces = other.boardPieces;
    }

    public void nextPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
        boardPieces++;
    }

    public boolean getSwap() {
        return swap;
    }

    public void swapTurnDecrement(){
        boardPieces--;
    }
    public void doubleTurnIncrement(){
        boardPieces++;
    }

}
