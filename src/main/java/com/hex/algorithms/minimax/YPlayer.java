package com.hex.algorithms.minimax;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

public class YPlayer extends MiniMax implements Algorithm {
    public YPlayer(){};
    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations, boolean swap) {
        Position startPos = new YPosition(board);
        int depth;

        depth = switch (iterations) {
            case 50_000 -> 1;
            case 250_000 -> 3;
            case 1_000_000 -> 4;
            case 5_000_000 -> 5;
            default -> 3;
        };

        MoveValue suggestedMove = findBestMove(startPos, player, depth);
        return new BoardCoordinate(suggestedMove.move.x, suggestedMove.move.y);
    }
}
