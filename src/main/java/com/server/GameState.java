package com.server;

// Made by Oscar
// Server version of GameState from the com/hex/GameState.java
// Manages swap, the current player and client, and whether the game is finished or not.
public class GameState {
    private int currentPlayer;
    private int currentClient;
    private boolean gameFinished = false;
    private boolean swap = true;

    GameState(int currentClient) {
        this.currentClient = currentClient;
        currentPlayer = 1;
    }

    public void setSwap(boolean swap) {
        this.swap = swap;
    }

    public boolean getSwap() {
        return swap;
    }


    public int getCurrentPlayer() {
        return currentPlayer;
    }
    public int getCurrentClient() {
        return currentClient;
    }

    public void nextPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
        currentClient = currentClient == 1 ? 2 : 1;
    }
    public boolean isGameFinished(){
        return gameFinished;
    }
    public void setGameFinished(boolean gameFinished){
        this.gameFinished = gameFinished;
    }
}
