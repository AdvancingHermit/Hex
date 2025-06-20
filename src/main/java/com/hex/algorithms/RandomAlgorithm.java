package com.hex.algorithms;

import com.hex.GameState;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//Made by Oliver

public class RandomAlgorithm implements Algorithm{
    Random random = new Random();
    // takes a random number from 0 to the number of pieces on the board - 1,
    //iterate through the empty spots until the indexes are equal then place a piece and return
    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations, boolean swap) {
        int rows = board.getRows();
        int cols = board.getCols();
        int emptyCount =  cols * rows - gameState.getBoardPieces();

        if (emptyCount == 0) {
            throw new IllegalStateException("No empty cells available on the board");
        }



        int targetIndex = random.nextInt(emptyCount);


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


        return null;


        }


}
