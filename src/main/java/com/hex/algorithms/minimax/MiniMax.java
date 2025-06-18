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

    protected final int maxDepth = 3;

    private HashMap<BigInteger, Float> evalMap;

    public MiniMax(){}

    public Move findBestMove(Position position, int player) {
        float bestValue = Float.NEGATIVE_INFINITY;
        Move bestMove = null;

        evalMap = new HashMap<>(Integer.MAX_VALUE);

        int nThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        List<Future<MoveValue>> futures = new ArrayList<>();
        System.out.println(nThreads);

        ArrayList<Move> ogPossibleMoves = position.getPossibleMoves();

        int k = 0;

        while (k < ogPossibleMoves.size()) {
            for (int i = 0; i < nThreads; i++) {
                if (ogPossibleMoves.size() == k) { break; }
                Move move = ogPossibleMoves.get(k);
                SetHolder setHolder = new SetHolder();
                Position newPosition = position.Move(move, player);
                Callable<MoveValue> task = () -> evalWrapper(newPosition, maxDepth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, false, 3 - player, player, setHolder, move);
                futures.add(executor.submit(task));
                k++;
            }
        }

        List<MoveValue> evals = new ArrayList<>();
        for (Future<MoveValue> future : futures) {
            try {
                evals.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        for (MoveValue moveVal : evals){
            if (moveVal.value > bestValue){
                bestMove = moveVal.move;
                bestValue = moveVal.value;
            }
        }
        /*

        for (Move move : position.getPossibleMoves()) {
            Position newPosition = position.Move(move, player);
            System.out.println(move);
            SetHolder setHolder = new SetHolder();
            float value = alphabeta(newPosition, maxDepth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, false, 3 - player, player, setHolder);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }*/

        System.out.println("Move: " + bestMove + " -> Value: " + bestValue);
        if (bestMove == null){
            return position.getPossibleMoves().get(0);
        }
        return bestMove;
    }

    private static class MoveValue {
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

    private float alphabeta(Position pos, int depth, float alpha, float beta, boolean maximizingPlayer, int currentPlayer, int originalPlayer, SetHolder setHolder) {

        if (pos.checkWin(originalPlayer)) { return Float.POSITIVE_INFINITY; }
        if (pos.checkWin(3 - originalPlayer)) { return Float.NEGATIVE_INFINITY; }


        if (depth == 0 || pos.getPossibleMoves().isEmpty()) {

            //BigInteger key = getEvalmapKey(pos);
            //if (evalMap.containsKey(key)){
            //    return evalMap.get(key);
            //}
            float eval = pos.evaluate(originalPlayer, setHolder);
            //evalMap.put(key, eval);
            return eval;
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
