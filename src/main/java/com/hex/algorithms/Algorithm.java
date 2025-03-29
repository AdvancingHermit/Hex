package com.hex.algorithms;

import com.hex.GameState;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Algorithm {
    BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations);

    default BoardCoordinate makeRandomValidMove(int player, Board board){
        List<BoardCoordinate> moves = new ArrayList<>();
        for (int i = 0; i < board.cols; i++ ) {
            for (int j = 0; j < board.rows; j++ ) {
                if (board.getPiece(i,j) == 0) {
                    moves.add(new BoardCoordinate(i,j));
                }
            }
        }
        Collections.shuffle(moves);
        return moves.get(0);
    }
}

