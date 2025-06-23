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

// Christian
// Standard alphabeta enhanced minimax search with threading.
public class MiniMax {

    public MiniMax(){}

    public MoveValue findBestMove(Position position, int player, int depth) {
        float bestValue = Float.NEGATIVE_INFINITY;
        Move bestMove = null;

        int nThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        ArrayList<Move> ogPossibleMoves = position.getPossibleMoves();
        List<MoveValue> evals = new ArrayList<>();

        List<Callable<MoveValue>> tasks = new ArrayList<>(ogPossibleMoves.size());
        for (Move move : ogPossibleMoves){
            SetHolder setHolder = new SetHolder();
            Position newPosition = position.Move(move, player);
            BoardEvals boardEval = new BoardEvals();
            tasks.add(() -> evalWrapper(newPosition, depth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, false, 3 - player, player, setHolder, move, boardEval));
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
            //System.out.println("Something Wrong Happened");
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
    static class BoardEvals {
        HashMap<BigInteger, Float> alreadyComputedEvals;
        BoardEvals() {
            alreadyComputedEvals = new HashMap<>(10000);
        }
        public void addKey(BigInteger key, float eval){
            alreadyComputedEvals.put(key, eval);
        }
    }

    private MoveValue evalWrapper(Position pos, int depth, float alpha, float beta, boolean maximizingPlayer, int currentPlayer, int originalPlayer, SetHolder setHolder, Move move, BoardEvals boardEvals){
        return new MoveValue(move, alphabeta(pos, depth, alpha, beta, maximizingPlayer, currentPlayer, originalPlayer, setHolder, boardEvals));
    }

    private float alphabeta(Position pos, int depth, float alpha, float beta, boolean maximizingPlayer, int currentPlayer, int originalPlayer, SetHolder setHolder, BoardEvals boardEvals) {

        if (pos.checkWin(originalPlayer)) { return Float.POSITIVE_INFINITY; }
        if (pos.checkWin(3 - originalPlayer)) { return Float.NEGATIVE_INFINITY; }


        if (depth == 0 || pos.getPossibleMoves().isEmpty()) {
            BigInteger key = pos.getHashCode();
            if (boardEvals.alreadyComputedEvals.containsValue(key)) return boardEvals.alreadyComputedEvals.get(key);
            float eval = pos.evaluate(originalPlayer, setHolder);
            boardEvals.addKey(key, eval);
            return eval;
        }

        if (maximizingPlayer) {
            float value = Float.NEGATIVE_INFINITY;
            for (Move move : pos.getPossibleMoves()) {
                Position nextPos = pos.Move(move, currentPlayer);

                value = Math.max(value, alphabeta(nextPos, depth - 1, alpha, beta, false, 3 - currentPlayer, originalPlayer, setHolder, boardEvals));
                if (value >= beta) break;
                alpha = Math.max(alpha, value);

            }
            return value;
        } else {
            float value = Float.POSITIVE_INFINITY;
            for (Move move : pos.getPossibleMoves()) {
                Position nextPos = pos.Move(move, currentPlayer);
                value = Math.min(value, alphabeta(nextPos, depth - 1, alpha, beta, true, 3 - currentPlayer, originalPlayer, setHolder, boardEvals));
                if (value <= alpha) break;
                beta = Math.min(beta, value);
            }
            return value;
        }
    }
}
