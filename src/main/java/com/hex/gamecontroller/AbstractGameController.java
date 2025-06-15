package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import lombok.extern.java.Log;

@Log
public abstract class AbstractGameController implements Controller {

    protected static AbstractGameController INSTANCE;

    protected Board board;
    protected GameState gameState;
    protected Algorithm algorithm;
    protected boolean swap;
    protected int iterations;

    public static AbstractGameController getInstance() {
        return INSTANCE;
    }


    protected AbstractGameController(Board board, GameState gameState, Algorithm algorithm, boolean algoStart, int algoIterations, boolean swap) {
        this.board = board;
        this.gameState = gameState;
        this.algorithm = algorithm;
        this.iterations = algoIterations;
        this.swap = swap;
        if (algoStart) {
            this.algoStart(board, gameState, algorithm);
        }
    }

    private void algoStart(Board board, GameState gameState, Algorithm algorithm) {
        BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations, swap);
        placePiece(move);
        gameState.nextPlayer();
    }

    public abstract void gameIteration(BoardCoordinate co);

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
                gameState.swapTurnDecrement();
        }


    }

    private void updateBoard(int player) {
        if (board.checkWin(player)) {
            System.out.println("Player " + gameState.getCurrentPlayer() + " won");
            gameState.setGameFinished(true);
        }
    }


}

