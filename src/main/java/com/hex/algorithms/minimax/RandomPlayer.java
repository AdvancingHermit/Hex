package com.hex.algorithms.minimax;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

public class RandomPlayer extends MiniMax implements Algorithm {
    public RandomPlayer(){};
    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations) {
        Position startPos = new RandomPosition(board);
        Move suggestedMove = findBestMove(startPos, player);
        return new BoardCoordinate(suggestedMove.x, suggestedMove.y);
    }
}
