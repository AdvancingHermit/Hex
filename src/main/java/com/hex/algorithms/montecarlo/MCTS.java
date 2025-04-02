package com.hex.algorithms.montecarlo;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import com.hex.gamecontroller.SimulationController;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
@Log
public class MCTS implements Algorithm {

    private final static double exploreConstant = Math.sqrt(2);
    private boolean gameOver;

    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations) {
        Node root = new Node(null, new ArrayList<>(), 0, null);
        int nThreads = 1;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        List<Future<Node>> futures = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) {
            int n = i;
            Callable<Node> task = () -> makeTree(board, gameState, iterations, new Node(null, new ArrayList<>(), 0, null), n);
            futures.add(executor.submit(task));
        }

        List<Node> trees = new ArrayList<>();
        for (Future<Node> future : futures) {
            try {
                trees.add(future.get());
            } catch (InterruptedException |  ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();


        for (Node tree : trees){
            for (Node child : tree.children){
                root.mergeChild(child);
            }
        }
        for (Node child : root.children){
            child.value = UCT(child.winStats.wins, child.winStats.nSims, root.winStats.nSims, exploreConstant);
        }

        BoardCoordinate bestMove = maxNode(root).move;

        return bestMove;
    }

    private Node makeTree(Board board, GameState gameState, int iterations, Node root, int n) {
        int i = 0;
        log.info("start " + n);
        while (i < iterations + 1){
            gameOver = false;
            Board simBoard = new Board(board.getRows(), board.getCols());
            simBoard.setBoard(Arrays.stream(board.getBoard())
                    .map(int[]::clone)
                    .toArray(int[][]::new));
            SimulationController simulationController = new SimulationController(simBoard, new GameState(gameState));
            Node selectedNode = selection(root, simulationController);
            expansion(selectedNode, simBoard, simulationController);
            if (i ==0){
                i++;
                continue;
            }
           // System.out.println(selectedNode.value);
            int win = simulation(simulationController);
            backpropagation(win, selectedNode);
            i++;
        }
        log.info("done " + n);
        return root;
    }

    private Node selection(Node root, SimulationController simulationController){
        Node cur = root;
        while (!cur.children.isEmpty()){
             cur = maxNode(cur);
             simulationController.placePiece(cur.move);
        }
        return cur;

    }

    private void expansion(Node leaf, Board simBoard, SimulationController simulationController){
        if (!simulationController.getGameState().isGameFinished()) {
            List<BoardCoordinate> moves = possibleMoves(simBoard);
            for (BoardCoordinate move : moves) {
                Node child = new Node(leaf, new ArrayList<>(), Integer.MAX_VALUE, move);
                leaf.addChild(child);
            }
        } else {
            gameOver = true;
        }

    }

    private int simulation(SimulationController simulationController){
        if (!gameOver) {
            int upPlayer = simulationController.getGameState().getCurrentPlayer();

            while (!simulationController.getGameState().isGameFinished()) {
                simulationController.randomMove();
            }
            int win = simulationController.getGameState().getCurrentPlayer() != upPlayer ? 1 : -1;
            // int win = 1;
            //win = win * val;
            return win;
        } else {
            return 1;
        }


    }

    private void backpropagation(int win, Node leaf){
        Node cur = leaf;
        while (true){
            cur.winStats.wins += win;
            cur.winStats.nSims += 1;
            List<Node> children = visitedChildren(cur);
            for (Node child : children){
                child.value = UCT(child.winStats.wins, child.winStats.nSims, cur.winStats.nSims, exploreConstant);
            }
            if (cur.parent == null){
                break;
            }
            cur.value = UCT(cur.winStats.wins, cur.winStats.nSims, cur.parent.winStats.nSims+1, exploreConstant);
            win = -1 * win;
            cur = cur.parent;
        }
    }

    private double UCT(int wins, int nSims, int parSims, double c){
        return ((double) wins / (double) nSims) + c * Math.sqrt(Math.log(parSims)/nSims);
    }

    private Node maxNode(Node cur){
        double maxVal = -10000;
        Node bestNode = new Node(null,null,0, null);
        for (Node child : cur.children){
            if (child.value > maxVal){
                bestNode = child;
                maxVal = child.value;
            }
        }
        return bestNode;
    }

    private ArrayList<BoardCoordinate> possibleMoves(Board simBoard){
        ArrayList<BoardCoordinate> moves = new ArrayList<>();
        for (int i = 0; i < simBoard.getCols(); i++ ) {
            for (int j = 0; j < simBoard.getRows(); j++ ) {
                if (simBoard.getPiece(i,j) == 0) {
                    moves.add(new BoardCoordinate(i,j));
                }
            }
        }
        return moves;
    }

    private List<Node> visitedChildren(Node node) {
        return node.children.stream().filter(n -> n.value < Integer.MAX_VALUE).toList();
    }

}
