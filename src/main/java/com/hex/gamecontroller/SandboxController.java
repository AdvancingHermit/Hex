package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import lombok.extern.java.Log;


@Log
public class SandboxController extends Controller {

    private static SandboxController INSTANCE;

    private final Board board;
    private final GameState gameState;
    private Runnable updateLabel;

    public static void createSandboxController(Board board, GameState gameState, Runnable updateLabel) {
        INSTANCE = new SandboxController(board, gameState, updateLabel);
    }

    private SandboxController(Board board, GameState gameState, Runnable updateLabel) {
        this.board = board;
        this.gameState = gameState;
        this.updateLabel = updateLabel;
    }

    public static SandboxController getInstance() {
        return INSTANCE;
    }

    @Override
    public void gameIteration(BoardCoordinate co) {
        placePiece(co);
        gameState.nextPlayer();
    }

    public void gameIteration(BoardCoordinate co, boolean test) {
        placePiece(co);
        gameState.nextPlayer();
    }

    public void gameIteration(BoardCoordinate co, Runnable updateLabel) {
        placePiece(co);
        gameState.nextPlayer();
        updateLabel.run();
    }

    private void cleanBoard(){
        for (int x = 0; x < board.getCols(); x++){
            for (int y = 0; y < board.getRows(); y++){
                if (board.getPiece(x, y) > 2) board.setPiece(x, y, 0);
            }
        }
    }

    @Override
    public void placePiece(BoardCoordinate co) {
        System.out.println("SumSum");
        int player = gameState.getCurrentPlayer();
        int x = co.x;
        int y = co.y;
        log.info("Hex move: player=" + player + " at (" + x + "," + y + ")");
        cleanBoard();
        board.setPiece(x, y, player);
        updateBoard(player);

        if (!gameState.isGameFinished()) {
            gameState.nextPlayer();

        }
        updateLabel.run();
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

