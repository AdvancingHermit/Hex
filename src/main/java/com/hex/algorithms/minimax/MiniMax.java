package com.hex.algorithms.minimax;

public class MiniMax {

    protected final int maxDepth = 4;

    public MiniMax(){}

    public Move findBestMove(Position position, int player) {
        float bestValue = Float.NEGATIVE_INFINITY;
        Move bestMove = null;

        if (position.getPossibleMoves().size() == position.getSize()){
            bestMove = position.getMiddleMove();
            Position newPosition = position.Move(bestMove, player);
            bestValue = alphabeta(newPosition, maxDepth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, false, 3 - player, player);
        }

        for (Move move : position.getPossibleMoves()) {
        //Move move = new Move(0, 0);
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

    private float alphabeta(Position pos, int depth, float alpha, float beta, boolean maximizingPlayer, int currentPlayer, int originalPlayer) {

        if (pos.checkWin(originalPlayer))   return Float.POSITIVE_INFINITY;
        if (pos.checkWin(3 - originalPlayer))     return Float.NEGATIVE_INFINITY;


        if (depth == 0 || pos.getPossibleMoves().isEmpty()) {
            return pos.evaluate(originalPlayer);
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
