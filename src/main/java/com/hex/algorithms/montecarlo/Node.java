package com.hex.algorithms.montecarlo;

import com.hex.components.BoardCoordinate;

import java.util.List;
//Made by Oliver

public class Node {
    public Node parent;
    public List<Node> children;
    public double value;
    public BoardCoordinate move;
    public boolean swap;
    public int wins = 0;
    public int nSims = 0;

    //Used to represent a tree
    public Node(Node parent, List<Node> children, double value, BoardCoordinate move, boolean swap) {
        this.parent = parent;
        this.children = children;
        this.value = value;
        this.move = move;
        this.swap = swap;
    }

    public void addChild(Node child){
        children.add(child);
    }

    //used to merge the parallel trees from root parallelization
    public void mergeChild(Node child){
        for (Node pChild : children){
            if (pChild.move.x == child.move.x && pChild.move.y == child.move.y){
                pChild.wins += child.wins;
                pChild.nSims += child.nSims;

                nSims += child.nSims;
                return;
            }
        }
        addChild(child);
        nSims += child.nSims;
    }

}
