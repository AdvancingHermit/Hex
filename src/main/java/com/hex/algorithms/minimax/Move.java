package com.hex.algorithms.minimax;

import java.util.Objects;

public class Move {
    public int x;
    public int y;

    public Move(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return x == move.x && y == move.y;
    }

    @Override
    public int hashCode() {
        return x * 14 + y; // Antager at x og y har vals [0; 11]
    }

    public int val(int mult){
        return x + y * mult;
    }

    public Move addGet(int x, int y){
        return new Move(this.x + x, this.y + y);
    }
    public Move subGet(int x, int y){
        return new Move(this.x - x, this.y - y);
    }
    public Move subGet(Move other){
        return new Move(this.x - other.x, this.y - other.y);
    }

    @Override
    public String toString(){
        return x + ", " + y;
    }

}
