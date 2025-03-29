package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import com.hex.components.Piece;


public class GameController implements Controller {
    private static Board board;
    private static GameState gameState;
    private static Algorithm algorithm;
    private static boolean swap = true;
    private static int iterations = 250_000;


    public GameController(Board board, GameState gameState, Algorithm algorithm){
        GameController.board = board;
        GameController.gameState = gameState;
        GameController.algorithm = algorithm;
        algoStart(board, gameState, algorithm);
    }

    private static void algoStart(Board board, GameState gameState, Algorithm algorithm) {
        BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations);
        placePiece(move);
        gameState.nextPlayer();
    }

    public static void gameIteration(BoardCoordinate co) {
        //System.out.println("works");
        if (gameState.isGameFinished()) {
            return;
        }
        placePiece(co);
        if (algorithm != null && !gameState.isGameFinished()) {
            gameState.nextPlayer();
            BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations);
            placePiece(move);
        }

        if (!gameState.isGameFinished()){
            gameState.nextPlayer();
        }
    }


    private static void placePiece(BoardCoordinate co) {
        int player = gameState.getCurrentPlayer();
        int x = co.x;
        int y = co.y;
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

