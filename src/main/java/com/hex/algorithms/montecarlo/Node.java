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

}
