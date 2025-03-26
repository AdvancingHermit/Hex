package com.hex;

public class GameState {

    private int currentPlayer = 1;
    private int playerNum;
    private boolean gameFinished = false;

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
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
