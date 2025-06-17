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
        this.carrier = new HashSet<>(carrier);
        this.depth = depth;
        criticalCell = null;
    }

    public VirtualConnection(Move x, Move y, Set<Move> carrier, int depth, Move criticalCell){
        this.x = x;
        this.y = y;
        this.carrier = new HashSet<>(carrier);
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
        return equalEnds(other) && Objects.equals(carrier, other.carrier);
    }


    public boolean equalEnds(VirtualConnection other) {
        return (x.equals(other.x) && y.equals(other.y)) || (y.equals(other.x) && x.equals(other.y));
    }

    public boolean sameCriticalCell(VirtualConnection other) {
        if (criticalCell == null || other.criticalCell == null) return false;
        return criticalCell.equals(other.criticalCell);
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
    public boolean isSubset(VirtualConnection other){
        if (other.carrier.size() > carrier.size() && equalEnds(other)){
            for (Move elem : carrier){
                if (!other.carrier.contains(elem)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean endIsCarrier(VirtualConnection other){
        return other.carrier.contains(x) || other.carrier.contains(y);
    }

    @Override
    public String toString(){
        return "Pos X: " + x + ", Pos Y: " + y + " With Depth: " + depth;
    }

    @Override
    public int hashCode() {
        // Ensure symmetric ends (x,y) and (y,x) have same hash
        int endHash = x.hashCode() + y.hashCode();
        return endHash*10 + depth; // Er betydelig hurtigere og unik nok
    }
}