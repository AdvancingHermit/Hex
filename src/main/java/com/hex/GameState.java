package com.hex;

public class GameState {

    private int currentPlayer = 1;
    private boolean gameFinished = false;

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
