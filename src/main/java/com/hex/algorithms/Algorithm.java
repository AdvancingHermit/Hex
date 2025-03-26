package com.hex.algorithms;

import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

import java.util.ArrayList;
import java.util.Collections;

public interface Algorithm {
    public void makeMove(int player, Board board);

    public default void makeRandomValidMove(int player, Board board){
        ArrayList<BoardCoordinate> moves = new ArrayList<>();
        for (int i = 0; i < board.cols; i++ ) {
            for (int j = 0; j < board.rows; j++ ) {
                if (board.getPiece(i,j) == 0) {
                    moves.add(new BoardCoordinate(i,j));
                }
            }
        }
        Collections.shuffle(moves);
        int x = moves.get(0).x;
        int y = moves.get(0).y;
        board.setPiece(x,y,player);
    }
}

