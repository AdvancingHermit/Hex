package com.hex.algorithms.minimax;

import java.util.*;

public interface Position {

    static enum Colors {
        RED(2),
        BLUE(1);

        private final int value;

        Colors(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    final int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, -1}, {-1, 1} };
    public Position Move(Move move, int player);
    public float evaluate(int player);
    public int getSize();
    public void placeMove(Move move, int player);
    public void removeFromPossibleMoves(Move move);
    boolean checkWin(int player);
    ArrayList<Move> getPossibleMoves();

    void setplayerOnTurn(boolean playerOnTurn);

    Move getMiddleMove();

    public int[][] getBoard();
}
