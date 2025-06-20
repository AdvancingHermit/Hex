package com.hex.algorithms.minimax;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class VirtualConnection implements Comparable<VirtualConnection> {

    public Move x;
    public Move y;
    public Set<Move> carrier;
    public int depth;
    public Move criticalCell;

    public VirtualConnection(Move x, Move y, Set<Move> carrier, int depth){
        this.x = x;
        this.y = y;
        this.carrier = carrier;
        this.depth = depth;
        criticalCell = null;
    }

    public VirtualConnection(Move x, Move y, Set<Move> carrier, int depth, Move criticalCell){
        this.x = x;
        this.y = y;
        this.carrier = carrier;
        this.depth = depth;
        this.criticalCell = criticalCell;
    }

    public int compareTo(VirtualConnection other) {
        return this.depth - other.depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualConnection other = (VirtualConnection) o;
        return carrier.size() == other.carrier.size() && equalEnds(other) && Objects.equals(carrier, other.carrier);
    }


    public boolean equalEnds(VirtualConnection other) {
        return x.hashCode() + y.hashCode() == other.x.hashCode() + other.y.hashCode();
    }

    public boolean sameCriticalCell(VirtualConnection other) {
        if (criticalCell == null || other.criticalCell == null) return false;
        return criticalCell.equals(other.criticalCell);
    }

    public boolean endIsCriticalEdge() {
        if (criticalCell == null) return false;
        return criticalCell.equals(x) || criticalCell.equals(y);
    }

    public Move[] getConnectingEnd(VirtualConnection other) {
        if (equalEnds(other)) {
            return null;
        }

        if (x.equals(other.y)) return new Move[]{x, y, other.x};
        if (y.equals(other.x)) return new Move[]{y, x, other.y};
        if (x.equals(other.x)) return new Move[]{x, y, other.y};
        if (y.equals(other.y)) return new Move[]{y, x, other.x};

        return null;
    }


    // Tjekker om denne er sub af anden.
    public boolean isSubset(VirtualConnection other) {
        if (equalEnds(other) && other.carrier.size() > carrier.size()) {
            return other.carrier.containsAll(carrier);
        }
        return false;
    }

    // Tjekker om denne er sub af anden.
    public boolean isSubsetAssumed(VirtualConnection other) {
        if (other.carrier.size() > carrier.size()) {
            return other.carrier.containsAll(carrier);
        }
        return false;
    }

    public boolean isDistinct(VirtualConnection other){
        return !equalEnds(other) && !endIsCarrier(other) && !other.endIsCarrier(this);
    }

    public boolean distinctCarrier(VirtualConnection other){
        for (Move move : carrier){
            if (other.carrier.contains(move)) return false;
        }
        return true;
    }

    public boolean endIsCarrier(VirtualConnection other){
        return other.carrier.contains(x) || other.carrier.contains(y);
    }
    public boolean crititcalCellIsImportant(VirtualConnection other){
        if (other.carrier.size() < 3) return criticalCellIsCarrier(other);
        else return false;
    }
    public boolean criticalCellIsCarrier(VirtualConnection other){
        return other.carrier.contains(criticalCell);
    }

    @Override
    public String toString(){
        return "Pos X: " + x + ", Pos Y: " + y + " With Depth: " + depth;
    }

    public int getMovesCode() {
        // Ensure symmetric ends (x,y) and (y,x) have same hash
        return x.hashCode() + y.hashCode();
    }

    @Override
    public int hashCode() {
        // Ensure symmetric ends (x,y) and (y,x) have same hash
        int endHash = x.hashCode() + y.hashCode();
        return endHash*10 + depth; // Er betydelig hurtigere og unik nok
    }
}