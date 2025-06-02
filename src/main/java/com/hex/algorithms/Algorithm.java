package com.hex.algorithms;

import com.hex.GameState;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

public interface Algorithm {
    BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations, boolean swap);
}

