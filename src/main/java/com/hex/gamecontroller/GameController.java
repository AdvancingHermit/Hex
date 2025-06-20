package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
//Made by Oliver

@Log
public class GameController extends AbstractGameController {



    public static void createGameController(Board board, GameState gameState, Algorithm algorith, boolean algoStart, int algoIterations, boolean swap) {
        INSTANCE = new GameController(board, gameState, algorith, algoStart, algoIterations, swap);
    }

    private GameController(Board board, GameState gameState, Algorithm algorithm, boolean algoStart, int algoIterations, boolean swap) {
        super(board, gameState, algorithm, algoStart, algoIterations, swap);
    }
    
    public void gameIteration(BoardCoordinate co, Runnable updateLabel) {

        //System.out.println("works");
        if (gameState.isGameFinished() || (board.getPiece(co.x, co.y) != 0 && !swap)) {
            return;
        }
        placePiece(co);
       gameState.nextPlayer();
       updateLabel.run();

        new Thread(() ->{
            if (algorithm != null && !gameState.isGameFinished()) {
                BoardCoordinate move = algorithm.makeMove(gameState.getCurrentPlayer(), board, gameState, iterations, swap);
                placePiece(move);
                gameState.nextPlayer();
            }
            updateLabel.run();
        }).start();


    }


    @Override
    void gameIteration(BoardCoordinate coord) {
        throw new RuntimeException("Unexpected controller action");

    }
}

