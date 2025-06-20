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
    private boolean swap;

    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations, boolean swap) {
        Node root = new Node(null, new ArrayList<>(), 0, null, false);
        this.swap = swap;
        //multithreading
        int nThreads = Runtime.getRuntime().availableProcessors();
       //int nThreads = 1;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        List<Future<Node>> futures = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) {
            int n = i;
            Callable<Node> task = () -> makeTree(board, gameState, iterations/nThreads, new Node(null, new ArrayList<>(), 0, null, false), n);
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
            child.value = UCT(child.wins, child.nSims, root.nSims, exploreConstant);
        }

        BoardCoordinate bestMove = maxNode(root).move;
        Node debugNode = maxNode(root);
        return bestMove;
    }

    private Node makeTree(Board board, GameState gameState, int iterations, Node root, int n) {
        //makes the monte carlo tree given i iterations.
        int i = 0;
       // log.info("start " + n);
        while (i < iterations){
            gameOver = false;
            Board simBoard = new Board(board.getRows(), board.getCols());
            simBoard.setBoard(Arrays.stream(board.getBoard())
                    .map(int[]::clone)
                    .toArray(int[][]::new));
            SimulationController simulationController = new SimulationController(simBoard, new GameState(gameState));
            Node selectedNode = selection(root, simBoard, simulationController);
            selectedNode = expansion(selectedNode, simBoard, simulationController);
           // System.out.println(selectedNode.value);
            int win = simulation(simulationController);
            backpropagation(win, selectedNode);
            i++;
        }
       // log.info("done " + n);
        return root;
    }

    private Node selection(Node root, Board simBoard, SimulationController simulationController){
        //traverses tree and finds a leaf node
        Node cur = root;
        while (!cur.children.isEmpty() && cur.children.size() == possibleMoves(simBoard).size()){
             cur = maxNode(cur);
             if (cur.swap){
                 simulationController.removePiece(cur.move);
                 simulationController.placePiece(new BoardCoordinate(cur.move.y, cur.move.x));
             }
             else {
                 simulationController.placePiece(cur.move);
             }
        }
        return cur;

    }

    private Node expansion(Node leaf, Board simBoard, SimulationController simulationController){
        //expands the leaf with a possible move
        if (!simulationController.getGameState().isGameFinished()) {
            if (swap && simBoard.swapAvailable()) {
                BoardCoordinate swapMove = null;
                for (int i = 0; i < simBoard.getCols(); i++ ) {
                    for (int j = 0; j < simBoard.getRows(); j++ ) {
                        if (simBoard.getPiece(i,j) != 0) {
                            swapMove = new BoardCoordinate(i,j);
                        }
                    }
                }
                Node child = new Node(leaf, new ArrayList<>(), Integer.MAX_VALUE, swapMove, true);
                leaf.addChild(child);
                simulationController.removePiece(child.move);
                simulationController.placePiece(new BoardCoordinate(child.move.y, child.move.x));
                swap = false;
                return child;
            } else {
                List<BoardCoordinate> moves = possibleMoves(simBoard);
                Node child = new Node(leaf, new ArrayList<>(), Integer.MAX_VALUE, moves.get(leaf.children.size()), false);
                leaf.addChild(child);
                simulationController.placePiece(child.move);
                return child;
            }



        } else {
            gameOver = true;
            return leaf;
        }

    }

    private int simulation(SimulationController simulationController){
        //plays random games, and decides the winner based on the player who sent the game into this position
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
        //back propagetes results to the root
        Node cur = leaf;
        while (true){
            cur.wins += win;
            cur.nSims += 1;
            for (Node child : cur.children){
                child.value = UCT(child.wins, child.nSims, cur.nSims, exploreConstant);
            }
            if (cur.parent == null){
                break;
            }
            cur.value = UCT(cur.wins, cur.nSims, cur.parent.nSims+1, exploreConstant);
            win = -1 * win;
            cur = cur.parent;
        }
    }


    private double UCT(int wins, int nSims, int parSims, double c){
        return ((double) wins / (double) nSims) + c * Math.sqrt(Math.log(parSims)/nSims);
    }

    private Node maxNode(Node cur){
        //finds the max value child of a node
        double maxVal = -10000;
        Node bestNode = new Node(null,null,0, null, false);
        for (Node child : cur.children){
            if (child.value > maxVal){
                bestNode = child;
                maxVal = child.value;
            }
        }
        if (bestNode.move == null){
            throw new RuntimeException("Next node not found");
        }
        return bestNode;
    }

    private ArrayList<BoardCoordinate> possibleMoves(Board simBoard){
        ArrayList<BoardCoordinate> moves = new ArrayList<>(simBoard.getCols() * simBoard.getCols());
        for (int i = 0; i < simBoard.getCols(); i++ ) {
            for (int j = 0; j < simBoard.getRows(); j++ ) {
                if (simBoard.getPiece(i,j) == 0) {
                    moves.add(new BoardCoordinate(i,j));
                }
            }
        }
        return moves;
    }

}
