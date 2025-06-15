package com.hex.algorithms;

import com.hex.GameState;
import com.hex.algorithms.montecarlo.BoardCoordinateMoves;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

public interface Algorithm {
    BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations, boolean swap);
    default BoardCoordinateMoves makeDoubleMove(int player, Board board, GameState gameState, int iterations, boolean swap) {
        throw new RuntimeException("Not supported");
    }
}

