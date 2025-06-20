package com.hex.algorithms.minimax;

import com.hex.algorithms.minimax.connectionsHelpers.SetHolder;
import com.hex.algorithms.montecarlo.Node;
import com.hex.components.Board;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class MiniMax {

    public MiniMax(){}

    public MoveValue findBestMove(Position position, int player, int depth) {
        float bestValue = Float.NEGATIVE_INFINITY;
        Move bestMove = null;


        /*
        Board board = new Board(7, 7);
        board.setPiece(4, 1, 1);
        board.setPiece(3, 3, 1);
        board.setPiece(2, 5, 1);

        board.setPiece(1, 1, 2);
        board.setPiece(1, 2, 2);

        position = new ConnectionPosition(board); */


        int nThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        System.out.println(nThreads);

        ArrayList<Move> ogPossibleMoves = position.getPossibleMoves();
        List<MoveValue> evals = new ArrayList<>();

        List<Callable<MoveValue>> tasks = new ArrayList<>(ogPossibleMoves.size());
        for (Move move : ogPossibleMoves){
            SetHolder setHolder = new SetHolder();
            Position newPosition = position.Move(move, player);
            tasks.add(() -> evalWrapper(newPosition, depth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, false, 3 - player, player, setHolder, move));
        }
        try {
            List<Future<MoveValue>> futures = executor.invokeAll(tasks);
            for (Future<MoveValue> future : futures) {
                try {
                    evals.add(future.get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            for (MoveValue moveVal : evals){
                if (moveVal.value > bestValue){
                    bestMove = moveVal.move;
                    bestValue = moveVal.value;
                }
            }
        }
        System.out.println("Move: " + bestMove + " -> Value: " + bestValue);
        if (bestMove == null){
            System.out.println("Something Wrong Happened");
            return new MoveValue(position.getPossibleMoves().get(0), 0);
        }
        return new MoveValue(bestMove, bestValue);
    }

    static class MoveValue {
        Move move;
        float value;
        MoveValue(Move move, float value) {
            this.move = move;
            this.value = value;
        }
    }

    private MoveValue evalWrapper(Position pos, int depth, float alpha, float beta, boolean maximizingPlayer, int currentPlayer, int originalPlayer, SetHolder setHolder, Move move){
        return new MoveValue(move, alphabeta(pos, depth, alpha, beta, maximizingPlayer, currentPlayer, originalPlayer, setHolder));
    }

    private float alphabeta(Position pos, int depth, float alpha, float beta, boolean maximizingPlayer, int currentPlayer, int originalPlayer, SetHolder setHolder) {

        if (pos.checkWin(originalPlayer)) { return Float.POSITIVE_INFINITY; }
        if (pos.checkWin(3 - originalPlayer)) { return Float.NEGATIVE_INFINITY; }


        if (depth == 0 || pos.getPossibleMoves().isEmpty()) {
            return pos.evaluate(originalPlayer, setHolder);
        }

        if (maximizingPlayer) {
            float value = Float.NEGATIVE_INFINITY;
            for (Move move : pos.getPossibleMoves()) {
                Position nextPos = pos.Move(move, currentPlayer);

                value = Math.max(value, alphabeta(nextPos, depth - 1, alpha, beta, false, 3 - currentPlayer, originalPlayer, setHolder));
                if (value >= beta) break;
                alpha = Math.max(alpha, value);

            }
            return value;
        } else {
            float value = Float.POSITIVE_INFINITY;
            for (Move move : pos.getPossibleMoves()) {
                Position nextPos = pos.Move(move, currentPlayer);
                value = Math.min(value, alphabeta(nextPos, depth - 1, alpha, beta, true, 3 - currentPlayer, originalPlayer, setHolder));
                if (value <= alpha) break;
                beta = Math.min(beta, value);
            }
            return value;
        }
    }
}
