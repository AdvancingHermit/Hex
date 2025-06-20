package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import lombok.extern.java.Log;
import java.util.Timer;


// Christian
@Log
public class AlgorithmNoUIController extends Controller {

    private static AlgorithmNoUIController INSTANCE;

    private final Board board;
    private final GameState gameState;
    private final Algorithm algorithm;
    private final Algorithm otherAlgo;

    private int algo1Won;
    private int algo2Won;

    private boolean swap = true;
    private int startingIterations;
    private int secondIterations;

    public static AlgorithmNoUIController getInstance() {
        return INSTANCE;
    }

    public static void createGameController(Board board, GameState gameState,
                                            Algorithm algorithm, Algorithm otherAlgo, int startingIterations, int secondIterations) {
        INSTANCE = new AlgorithmNoUIController(board, gameState, algorithm, otherAlgo, startingIterations, secondIterations);
    }

    private AlgorithmNoUIController(Board board, GameState gameState,
                                    Algorithm algorithm, Algorithm otherAlgo, int startingIterations, int secondIterations) {
        this.board = board;
        this.gameState = gameState;
        this.algorithm = algorithm;
        this.otherAlgo = otherAlgo;
        this.startingIterations = startingIterations;
        this.secondIterations = secondIterations;
        gameIteration(new BoardCoordinate(0,0));
    }

    // Loops through 50 games and then outputs the finished boardstates and how many of which algo won.
    public void gameIteration(BoardCoordinate ignored) {
        String output = "";
        for (int i = 0; i < 1; i++) {
            board.reset();
            gameState.reset();
            while (!gameState.isGameFinished()) {
                if (gameState.getCurrentPlayer() == 1) {
                    BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, startingIterations, false);
                    placePiece(move);

                } else if (gameState.getCurrentPlayer() == 2) {
                    BoardCoordinate otherMove = otherAlgo.makeMove(gameState.getCurrentPlayer(), board, gameState, secondIterations, false);
                    placePiece(otherMove);
                }
            }
            output += board.printBoard();
            output += updateBoard(gameState.getCurrentPlayer());
        }
        System.out.println(output);
        System.out.println("Algo1 Won " + algo1Won/2 + " Algo2 Won " + algo2Won/2);
    }

    @Override
    public void placePiece(BoardCoordinate co) {
        int player = gameState.getCurrentPlayer();
        int x = co.x;
        int y = co.y;
        //log.info("Hex move: player=" + player + " at (" + x + "," + y + ")");

        if (board.getPiece(x, y) == 0 || swap) {
            swap = false;
            board.setPiece(x, y, player);
            updateBoard(player);
        }
        if (!gameState.isGameFinished()) {
            gameState.nextPlayer();
        }
        //notifyMoveListener(co);
    }

    private String updateBoard(int player) {
        if (board.checkWin(player)) {
            gameState.setGameFinished(true);
            if (player == 1) algo1Won += 1;
            if (player == 2) algo2Won += 1;
            //System.out.println("Player " + player + " won");
            return ("Player " + player + " won\n" );
        }
        return "";
    }
}
