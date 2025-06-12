package com.hex.algorithms.minimax.connectionsHelpers;

import com.hex.GameState;
import com.hex.algorithms.minimax.Move;
import com.hex.algorithms.minimax.VirtualConnection;
import com.hex.components.Board;

import java.util.ArrayList;

public class SimpleFuncs extends Board {

    protected ArrayList<Move> emptyCells;
    protected ArrayList<Move> blueCells;
    protected ArrayList<Move> redCells;

    protected ArrayList<VirtualConnection> blueVCs;
    protected ArrayList<VirtualConnection> redVCs;
    protected ArrayList<VirtualConnection> blueSemiVCs;
    protected ArrayList<VirtualConnection> redSemiVCs;

    protected int[][] elecBoard;
    protected int elecRows;
    protected int elecCols;

    protected static enum Colors {
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

    protected boolean movesOnBothBlueEdges(Move move1, Move move2) {
        return (move1.x == 0 && move2.x == elecCols - 1)
                || (move2.x == 0 && move1.x == elecCols - 1);
    }

    protected boolean movesOnBothRedEdges(Move move1, Move move2) {
        return (move1.y == 0 && move2.y == elecRows - 1)
                || (move2.y == 0 && move1.y == elecRows - 1);
    }

    protected boolean bothBlueMovesOnSameEdge(VirtualConnection vc) {
        return (vc.x.x == 0 && vc.y.x == 0) || (vc.x.x == elecCols - 1 && vc.y.x == elecCols - 1);
    }

    protected boolean bothRedMovesOnSameEdge(VirtualConnection vc) {
        return (vc.x.y == 0 && vc.y.y == 0) || (vc.x.y == elecRows - 1 && vc.y.y == elecRows - 1);
    }

    protected boolean movesOnOneBlueEdge(Move move1, Move move2) {
        return move1.x == 0 || move2.x == elecCols - 1 || move2.x == 0 || move1.x == elecCols - 1;
    }

    protected boolean movesOnOneRedEdge(Move move1, Move move2) {
        return move1.y == 0 || move2.y == elecRows - 1 || move2.y == 0 || move1.y == elecRows - 1;
    }

    protected boolean movesOnLeftBlueEdge(VirtualConnection vc) {
        return vc.x.x == 0 || vc.y.x == 0;
    }

    protected boolean movesOnRightBlueEdge(VirtualConnection vc) {
        return vc.x.x == elecCols - 1 || vc.y.x == elecCols - 1;
    }

    protected boolean movesOnLeftRedEdge(VirtualConnection vc) {
        return vc.x.y == 0 || vc.y.y == 0;
    }

    protected boolean movesOnRightRedEdge(VirtualConnection vc) {
        return vc.x.y == elecRows - 1 || vc.y.y == elecRows - 1;
    }


    public SimpleFuncs(int rows, int cols) {
        super(rows, cols);
    }

    public SimpleFuncs(int rows, int cols, GameState gameState) {
        super(rows, cols, gameState);
    }

    public SimpleFuncs(Board other) {
        super(other);
    }
}
