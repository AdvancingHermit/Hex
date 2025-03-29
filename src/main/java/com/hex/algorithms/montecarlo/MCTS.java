package com.hex.algorithms.montecarlo;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import com.hex.gamecontroller.AlgoController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCTS implements Algorithm {

    private final double exploreConstant = Math.sqrt(2);
    private Node root;
    private AlgoController algoController;

    Board board;
    int player;
    Board simBoard;
    GameState simGameState;
    int val = -1;
    boolean gameOver;

    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations) {
        root = new Node(null, new ArrayList<>(), 0, null);
        this.board = board;
        this.player = player;


        int i = 0;
        while (i<iterations+1){
            gameOver = false;
            simBoard = new Board(board.rows,board.cols);
            simBoard.board = Arrays.stream(board.board)
                    .map(int[]::clone)
                    .toArray(int[][]::new);
            simGameState = new GameState(gameState);
            algoController = new AlgoController(simBoard, simGameState, this);
            Node selectedNode = selection();
            expansion(selectedNode);
            if (i==0){
                i++;
                continue;
            }
           // System.out.println(selectedNode.value);
            int win = simulation();
            backpropagation(win, selectedNode);
            i++;
        }
        BoardCoordinate bestMove = maxNode(root).move;

        return bestMove;
    }

    private Node selection(){
        Node cur = root;
        while (!cur.children.isEmpty()){
             cur = maxNode(cur);
             algoController.placePiece(cur.move);
        }

        return cur;

    }

    private void expansion(Node leaf){
        if (!algoController.gameState.isGameFinished()) {
            List<BoardCoordinate> moves = possibleMoves();
            for (BoardCoordinate move : moves) {
                Node child = new Node(leaf, new ArrayList<>(), Integer.MAX_VALUE, move);
                leaf.addChild(child);
            }
        } else {
            gameOver = true;
        }

    }

    private int simulation(){
        if (!gameOver) {
            int upPlayer = algoController.gameState.getCurrentPlayer();

            while (!algoController.gameState.isGameFinished()) {
                algoController.randomMove();
            }
            int win = algoController.gameState.getCurrentPlayer() != upPlayer ? 1 : -1;
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

    private ArrayList<BoardCoordinate> possibleMoves(){
        ArrayList<BoardCoordinate> moves = new ArrayList<>();
        for (int i = 0; i < board.cols; i++ ) {
            for (int j = 0; j < board.rows; j++ ) {
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
