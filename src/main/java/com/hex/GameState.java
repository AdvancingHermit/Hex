package com.hex;

public class GameState {

    private int currentPlayer = 1;
    private int playerNum;
    private boolean gameFinished = false;
    private boolean swap = true;

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

    public void setSwap(boolean swap) {
        this.swap = swap;
    }

    public boolean getSwap() {
        return swap;
    }

    public boolean isGameFinished(){
        return gameFinished;
    }
    public void setGameFinished(boolean gameFinished){
        this.gameFinished = gameFinished;
    }
}
