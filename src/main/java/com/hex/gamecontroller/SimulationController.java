package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.RandomAlgorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import lombok.Getter;
import lombok.Setter;


public class SimulationController {
    private Board board;
    @Getter
    @Setter
    private GameState gameState;
    private Algorithm algorithm;


    public SimulationController(Board board, GameState gameState){
        this.board = board;
        this.setGameState(gameState);
        this.algorithm = new RandomAlgorithm();
    }

    public void randomMove() {
        BoardCoordinate move = algorithm.makeMove(getGameState().getCurrentPlayer(), board, getGameState(), 0);
        placePiece(move);
    }

    public void placePiece(BoardCoordinate co) {
        int player = getGameState().getCurrentPlayer();
        int x = co.x;
        int y = co.y;
        board.setPiece(x, y, player);
        updateBoard(player);
        if (!getGameState().isGameFinished()) {
            getGameState().nextPlayer();
        }
    }

    private void updateBoard(int player){
        if (board.checkWin(player)){
         //   System.out.println("Player " + gameState.getCurrentPlayer() + " won");
            getGameState().setGameFinished(true);
        }
    }
}
