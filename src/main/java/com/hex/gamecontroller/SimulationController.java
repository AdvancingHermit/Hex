package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.RandomAlgorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;


public class SimulationController {
    private Board board;
    public GameState gameState;
    private Algorithm algorithm;
    private boolean swap = true;



    public SimulationController(Board board, GameState gameState){
        this.board = board;
        this.gameState = gameState;
        this.algorithm = new RandomAlgorithm();
    }

    public void randomMove() {
        BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, 0);
        placePiece(move);

    }


    public void placePiece(BoardCoordinate co) {
        int player = gameState.getCurrentPlayer();
        int x = co.x;
        int y = co.y;
        board.setPiece(x, y, player);
        updateBoard(player);
        if (!gameState.isGameFinished()) {
            gameState.nextPlayer();
        }


    }

    private void updateBoard(int player){
        if (board.checkWin(player)){
         //   System.out.println("Player " + gameState.getCurrentPlayer() + " won");
            gameState.setGameFinished(true);
        }
    }



}
