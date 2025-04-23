package com.hex.algorithms.montecarlo;

import com.hex.components.BoardCoordinate;

import java.util.List;

public class Node {
    public Node parent;
    public List<Node> children;
    public double value;
    public BoardCoordinate move;
    public WinStats winStats;

    public Node(Node parent, List<Node> children, double value, BoardCoordinate move) {
        this.parent = parent;
        this.children = children;
        this.value = value;
        this.move = move;
        winStats = new WinStats(0,0);
    }

    public void addChild(Node child){
        children.add(child);
    }

    public void mergeChild(Node child){
        for (Node pChild : children){
            if (pChild.move.x == child.move.x && pChild.move.y == child.move.y){
                pChild.winStats.wins += child.winStats.wins;
                pChild.winStats.nSims += child.winStats.nSims;

                winStats.nSims += child.winStats.nSims;
                return;
            }
        }
        addChild(child);
        winStats.nSims += child.winStats.nSims;
    }

}
