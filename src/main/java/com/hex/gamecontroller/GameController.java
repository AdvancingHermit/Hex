package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.Piece;


public class GameController {
    private static Board board;
    private static GameState gameState;
    private static Algorithm algorithm;
    private static boolean swap = true;


    public GameController(Board board, GameState gameState, Algorithm algorithm){
        GameController.board = board;
        GameController.gameState = gameState;
        GameController.algorithm = algorithm;
    }

    public static void gameIteration(Piece hex) {
        //System.out.println("works");
        if (gameState.isGameFinished()) {
            return;
        }
        placePiece(hex);
        if (algorithm != null && !gameState.isGameFinished()) {
            gameState.nextPlayer();
            algorithm.makeRandomValidMove(gameState.getCurrentPlayer(), board);
            updateBoard(gameState.getCurrentPlayer());
        }

        if (!gameState.isGameFinished()){
            gameState.nextPlayer();
        }
    }


    private static void placePiece(Piece hex) {
        int player = gameState.getCurrentPlayer();
        int x = (int) hex.getGridPosition()[0];
        int y = (int) hex.getGridPosition()[1];
        System.out.println("Hex clicked! " + x + " " + y);
        if (board.getPiece(x, y) == 0 || swap){
            swap = false;
            board.setPiece(x, y, player);
            updateBoard(player);
        }

    }

    private static void updateBoard(int player){
        if (board.checkWin(player)){
            System.out.println("Player " + gameState.getCurrentPlayer() + " won");
            gameState.setGameFinished(true);
        }
    }



}

