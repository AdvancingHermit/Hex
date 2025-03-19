package com.server;

public class GameState {
    private int currentPlayer;
    private boolean gameFinished = false;

    GameState() {
        currentPlayer = 1;
    }
    GameState(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


    public int getCurrentPlayer() {
        return currentPlayer;
    }
    public void nextPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }
    public boolean isGameFinished(){
        return gameFinished;
    }
    public void setGameFinished(boolean gameFinished){
        this.gameFinished = gameFinished;
    }
}
