package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import lombok.extern.java.Log;
import java.util.Timer;
import java.util.TimerTask;
@Log
public class AlgorithmTesterController extends Controller {

    private static AlgorithmTesterController INSTANCE;

    private final Board board;
    private final GameState gameState;
    private final Algorithm algorithm;
    private final Algorithm otherAlgo;

    private boolean swap = true;
    private final int iterations = 100_000;
    private Timer timer = new Timer();

    public static AlgorithmTesterController getInstance() {
        return INSTANCE;
    }

    public static void createGameController(Board board, GameState gameState,
                                            Algorithm algorithm, Algorithm otherAlgo) {
        INSTANCE = new AlgorithmTesterController(board, gameState, algorithm, otherAlgo);
    }

    private AlgorithmTesterController(Board board, GameState gameState,
                                      Algorithm algorithm, Algorithm otherAlgo) {
        this.board = board;
        this.gameState = gameState;
        this.algorithm = algorithm;
        this.otherAlgo = otherAlgo;
        gameIteration(new BoardCoordinate(0,0));
    }

    public void gameIteration(BoardCoordinate ignored) {
        if (gameState.isGameFinished()) {
            return;
        }
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (gameState.getCurrentPlayer() == 2) {
                BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations, false);
                placePiece(move);

            } else if (gameState.getCurrentPlayer() == 1) {
                BoardCoordinate otherMove = otherAlgo.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations, false);
                placePiece(otherMove);
            } else {
                System.out.println("Wadddup");
            }
        }).start();
    }

    public void runGame() {
        // Start with first algorithm
        System.out.println("SUp");
        BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations, false);
        gameIteration(move); // This should trigger the listener for the next move
    }

    @Override
    public void placePiece(BoardCoordinate co) {
        int player = gameState.getCurrentPlayer();
        int x = co.x;
        int y = co.y;
        log.info("Hex move: player=" + player + " at (" + x + "," + y + ")");

        if (board.getPiece(x, y) == 0 || swap) {
            swap = false;
            board.setPiece(x, y, player);
            updateBoard(player);
        }
        if (!gameState.isGameFinished()) {
            gameState.nextPlayer();
        }
        notifyMoveListener(co);

    }

    private void updateBoard(int player) {
        if (board.checkWin(player)) {
            log.info("Player " + player + " won");
            System.out.println("Player " + player + " won");
            gameState.setGameFinished(true);
        }
    }
}
