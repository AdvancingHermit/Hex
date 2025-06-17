package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.montecarlo.BoardCoordinateMoves;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class DoublePieceController extends AbstractGameController {
    @Getter
    @Setter
    private int counter = 0;

    public static void createDoublePieceController(Board board, GameState gameState, Algorithm algorith, boolean algoStart, int algoIterations, boolean swap) {
        INSTANCE = new DoublePieceController(board, gameState, algorith, algoStart, algoIterations, swap);
    }

    private DoublePieceController(Board board, GameState gameState, Algorithm algorithm, boolean algoStart, int algoIterations, boolean swap) {
        super(board, gameState, algorithm, false, algoIterations, swap);
        this.swap = false;
        if (algoStart && algorithm != null){
            BoardCoordinateMoves moves = algorithm.makeDoubleMove(gameState.getCurrentPlayer(), board, gameState, iterations, false);
            placePiece(moves.firstMove());
            gameState.doubleTurnIncrement();
            if (!gameState.isGameFinished()) {
                placePiece(moves.secondMove());

            }
            gameState.nextPlayer();
        }
    }


    public void gameIteration(BoardCoordinate co) {
        //System.out.println("works");
        if (gameState.isGameFinished() || (board.getPiece(co.x, co.y) != 0 && !swap)) {
            return;
        }
        placePiece(co);
        if (getCounter() == 0){
            setCounter(1);
            gameState.doubleTurnIncrement();
            return;
        }
        setCounter(0);

        if (algorithm != null && !gameState.isGameFinished()) {
            gameState.nextPlayer();
            BoardCoordinateMoves moves = algorithm.makeDoubleMove(gameState.getCurrentPlayer(), board, gameState, iterations, false);
            placePiece(moves.firstMove());
            gameState.doubleTurnIncrement();
            if (!gameState.isGameFinished()) {
                placePiece(moves.secondMove());

            }
        }

        if (!gameState.isGameFinished()) {
            gameState.nextPlayer();
        }
    }


}

