package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import lombok.extern.java.Log;

@Log
public class GameController implements Controller {

    private static GameController INSTANCE;

    private Board board;
    private GameState gameState;
    private Algorithm algorithm;
    private boolean swap = false;
    private int iterations = 600_000;

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
       // this.algoStart(board, gameState, algorithm);
    }

    private void algoStart(Board board, GameState gameState, Algorithm algorithm) {
        BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations, swap);
        placePiece(move);
        gameState.nextPlayer();
    }

    public void gameIteration(BoardCoordinate co) {
        //System.out.println("works");
        if (gameState.isGameFinished() || board.getPiece(co.x, co.y) != 0) {
            return;
        }
        placePiece(co);
        if (algorithm != null && !gameState.isGameFinished()) {
            gameState.nextPlayer();
            BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations, swap);
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
        log.info("Hex clicked! " + x + " " + y);
        if (board.getPiece(x, y) == 0) {

            board.setPiece(x, y, player);
            updateBoard(player);
        } else if (swap && board.swapAvailable()){
                BoardCoordinate swapMove = null;
                for (int i = 0; i < board.getCols(); i++ ) {
                    for (int j = 0; j < board.getRows(); j++ ) {
                        if (board.getPiece(i,j) != 0) {
                            swapMove = new BoardCoordinate(i,j);
                        }
                    }
                }
                board.setPiece(swapMove.x,swapMove.y, 0);
                board.setPiece(swapMove.y, swapMove.x, player);
                swap = false;
        }


    }

    private void updateBoard(int player) {
        if (board.checkWin(player)) {
            System.out.println("Player " + gameState.getCurrentPlayer() + " won");
            gameState.setGameFinished(true);
        }
    }


}

