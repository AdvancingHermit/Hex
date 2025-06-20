package com.hex.algorithms.minimax;

import com.hex.algorithms.minimax.connectionsHelpers.SetHolder;

import java.math.BigInteger;
import java.util.*;

// Christian
public interface Position {

    static enum Colors {
        RED(1),
        BLUE(2);

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
    //public float evaluate(int player);
    public int getSize();

    public float evaluate(int player, SetHolder setHolder);

    public void placeMove(Move move, int player);
    public void removeFromPossibleMoves(Move move);
    boolean checkWin(int player);
    ArrayList<Move> getPossibleMoves();

    void setplayerOnTurn(boolean playerOnTurn);

    Move getMiddleMove();

    public int[][] getBoard();

    public void addPossibleMove(Move move);

    default BigInteger getHashCode() {
        BigInteger id = BigInteger.ZERO;
        for (int[] row : getBoard()) {
            for (int cell : row) {
                id = id.multiply(BigInteger.valueOf(3)).add(BigInteger.valueOf(cell));
            }
        }
        return id;
    }
}
