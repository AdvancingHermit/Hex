package com.hex;

import lombok.Getter;
import lombok.Setter;

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
    //gamestate used to represent all the needed attributes for the algorithms to produce a valid move

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

    //used to offset the fact that swap deletes a piece from the board
    public void swapTurnDecrement(){
        boardPieces--;
    }
    //used to offset th fact that double play play 2 pieces per move instead of 1
    public void doubleTurnIncrement(){
        boardPieces++;
    }

}
