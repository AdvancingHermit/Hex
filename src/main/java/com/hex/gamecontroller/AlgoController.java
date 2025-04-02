package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import com.hex.components.Piece;


public class AlgoController{
    private Board board;
    public GameState gameState;
    private Algorithm algorithm;
    private boolean swap = true;


    public AlgoController(Board board, GameState gameState, Algorithm algorithm){
        this.board = board;
        this.gameState = gameState;
        this.algorithm = algorithm;
    }

    public void randomMove() {
        BoardCoordinate move = algorithm.makeRandomValidMove(gameState.getCurrentPlayer(), board);
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
