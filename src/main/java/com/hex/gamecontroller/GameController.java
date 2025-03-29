package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;


public class GameController implements Controller {

    private static GameController INSTANCE;

    private Board board;
    private GameState gameState;
    private Algorithm algorithm;
    private boolean swap = true;
    private int iterations = 250_000;

    public static GameController getInstance() {
        return INSTANCE;
    }


    public static void createGameController(Board board, GameState gameState, Algorithm algorith) {
        INSTANCE = new GameController(board, gameState, algorith);
    }

    private GameController(Board board, GameState gameState, Algorithm algorithm) {
        this.board = board;
        this.gameState = gameState;
        this.algorithm = algorithm;
        this.algoStart(board, gameState, algorithm);
    }

    private void algoStart(Board board, GameState gameState, Algorithm algorithm) {
        BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations);
        placePiece(move);
        gameState.nextPlayer();
    }

    public void gameIteration(BoardCoordinate co) {
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

        if (!gameState.isGameFinished()) {
            gameState.nextPlayer();
        }
    }


    @Override
    public void placePiece(BoardCoordinate co) {
        int player = gameState.getCurrentPlayer();
        int x = co.x;
        int y = co.y;
        System.out.println("Hex clicked! " + x + " " + y);
        if (board.getPiece(x, y) == 0 || swap) {
            swap = false;
            board.setPiece(x, y, player);
            updateBoard(player);
        }

    }

    private void updateBoard(int player) {
        if (board.checkWin(player)) {
            System.out.println("Player " + gameState.getCurrentPlayer() + " won");
            gameState.setGameFinished(true);
        }
    }


}

