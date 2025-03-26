package com.server;

public class GameState {
    private int currentPlayer;
    private int currentClient;
    private boolean gameFinished = false;

    GameState() {
        currentPlayer = 1;
    }
    GameState(int currentClient) {
        this.currentClient = currentClient;
        currentPlayer = 1;
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
