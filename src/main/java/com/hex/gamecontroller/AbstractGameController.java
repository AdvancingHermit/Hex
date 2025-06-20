package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.function.Consumer;
//Made by Oliver

@Log
public abstract class AbstractGameController extends Controller {


    @Getter
    @Setter
    private int counter = 0;
    protected static AbstractGameController INSTANCE;

    protected Board board;
    @Getter
    protected GameState gameState;
    protected Algorithm algorithm;
    @Getter
    protected int algoPlayerNum;
    protected boolean swap;
    protected int iterations;

    public static AbstractGameController getInstance() {
        return INSTANCE;
    }

    //Abstract contoller to capture some of the basic behaivour, which are present in more controllers
    protected AbstractGameController(Board board, GameState gameState, Algorithm algorithm, boolean algoStart, int algoIterations, boolean swap) {
        this.board = board;
        this.gameState = gameState;
        this.algorithm = algorithm;
        this.iterations = algoIterations;
        this.swap = swap;
        if (algoStart) {
            this.algoPlayerNum = 1;
            this.algoStart(board, gameState, algorithm);
        } else {
            this.algoPlayerNum = 2;
        }
    }

    private void algoStart(Board board, GameState gameState, Algorithm algorithm) {
        if (algorithm == null){
            return;
        }
        BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations, swap);
        placePiece(move);
        gameState.nextPlayer();
    }

    public abstract void gameIteration(BoardCoordinate co, Runnable updateLabel);

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
        notifyMoveListener(co);

    }

    private void updateBoard(int player) {
        if (board.checkWin(player)) {
            System.out.println("Player " + gameState.getCurrentPlayer() + " won");
            gameState.setGameFinished(true);
        }
    }


}

