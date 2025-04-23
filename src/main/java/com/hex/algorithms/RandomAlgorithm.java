package com.hex.algorithms;

import com.hex.GameState;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomAlgorithm implements Algorithm{
    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations) {
        int rows = board.getRows();
        int cols = board.getCols();
        int emptyCount = 0;

// First pass: count empty cells
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (board.getPiece(i, j) == 0) {
                    emptyCount++;
                }
            }
        }

        if (emptyCount == 0) {
            throw new IllegalStateException("No empty cells available on the board");
        }

// Generate a random index from 0 to emptyCount - 1
        Random random = new Random();
        int targetIndex = random.nextInt(emptyCount);

// Second pass: find the target empty cell
        int currentIndex = 0;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (board.getPiece(i, j) == 0) {
                    if (currentIndex == targetIndex) {
                        return new BoardCoordinate(i, j);
                    }
                    currentIndex++;
                }
            }
        }

// This line should never be reached due to the emptyCount check
        return null;
        }
}
