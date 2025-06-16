package com.hex.algorithms.minimax;

import com.hex.components.Board;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class MiniMax {

    protected final int maxDepth = 1;

    private HashMap<BigInteger, Float> evalMap;

    public MiniMax(){}

    public Move findBestMove(Position position, int player) {
        float bestValue = Float.NEGATIVE_INFINITY;
        Move bestMove = null;

        evalMap = new HashMap<>(Integer.MAX_VALUE);

        Board forTesting = new Board(5, 5);

        forTesting.setPiece(0,4,1);
        forTesting.setPiece(2,2,1);
        forTesting.setPiece(3,3,1);

        forTesting.setPiece(0,2,2);
        forTesting.setPiece(2,4,2);
        forTesting.setPiece(4,1,2);

        //position = new ConnectionPosition(forTesting);

        if (position.getPossibleMoves().size() == position.getSize()*position.getSize()){
            bestMove = position.getMiddleMove();
            Position newPosition = position.Move(bestMove, player);
            bestValue = alphabeta(newPosition, maxDepth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, false, 3 - player, player);
        }

        for (Move move : position.getPossibleMoves()) {
            Position newPosition = position.Move(move, player);
            System.out.println(move);
            float value = alphabeta(newPosition, maxDepth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, false, 3 - player, player);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }
        System.out.println("Move: " + bestMove + " -> Value: " + bestValue);
        if (bestMove == null){
            return position.getPossibleMoves().get(0);
        }
        return bestMove;
    }

    private BigInteger getEvalmapKey(Position pos){
        int[][] currBoard = pos.getBoard();
        BigInteger id = BigInteger.ZERO;
        for (int[] row : currBoard) {
            for (int cell : row) {
                id = id.multiply(BigInteger.valueOf(3)).add(BigInteger.valueOf(cell));
            }
        }
        return id;
    }

    private float alphabeta(Position pos, int depth, float alpha, float beta, boolean maximizingPlayer, int currentPlayer, int originalPlayer) {

        if (pos.checkWin(originalPlayer)) { return Float.POSITIVE_INFINITY; }
        if (pos.checkWin(3 - originalPlayer)) { return Float.NEGATIVE_INFINITY; }


        if (depth == 0 || pos.getPossibleMoves().isEmpty()) {

            BigInteger key = getEvalmapKey(pos);
            if (evalMap.containsKey(key)){
                return evalMap.get(key);
            }
            float eval = pos.evaluate(originalPlayer);
            evalMap.put(key, eval);
            return eval;
        }

        if (maximizingPlayer) {
            float value = Float.NEGATIVE_INFINITY;
            for (Move move : pos.getPossibleMoves()) {
                Position nextPos = pos.Move(move, currentPlayer);

                value = Math.max(value, alphabeta(nextPos, depth - 1, alpha, beta, false, 3 - currentPlayer, originalPlayer));
                if (value >= beta) break;
                alpha = Math.max(alpha, value);

            }
            return value;
        } else {
            float value = Float.POSITIVE_INFINITY;
            for (Move move : pos.getPossibleMoves()) {
                Position nextPos = pos.Move(move, currentPlayer);
                value = Math.min(value, alphabeta(nextPos, depth - 1, alpha, beta, true, 3 - currentPlayer, originalPlayer));
                if (value <= alpha) break;
                beta = Math.min(beta, value);
            }
            return value;
        }
    }
}
