package com.hex.algorithms.minimax.sandboxHelpers;

import com.hex.algorithms.minimax.ConnectionPosition;
import com.hex.algorithms.minimax.Move;
import com.hex.algorithms.minimax.VirtualConnection;
import com.hex.algorithms.minimax.connectionsHelpers.SetHolder;
import com.hex.components.Board;

public class SandboxPosition extends ConnectionPosition {
    public VirtualConnection bestBlueVC;
    public VirtualConnection bestRedVC;
    public VirtualConnection bestSemiBlueVC;
    public VirtualConnection bestSemiRedVC;

    private void findEndVCs(){
        bestSemiBlueVC = board.getBestSemiBlueVC();
        bestSemiRedVC = board.getBestSemiRedVC();
        int bestDepth = 1000;
        for (VirtualConnection vc : board.getBlueVCs()){
            if (board.movesOnBothBlueEdges(vc.x, vc.y)){
                if (bestDepth > vc.depth){
                    bestBlueVC = vc;
                    bestDepth = vc.depth;
                }
            }
        }
        bestDepth = 1000;
        for (VirtualConnection vc : board.getBlueSemiVCs()){
            if (board.movesOnBothBlueEdges(vc.x, vc.y)){
                if (bestDepth > vc.depth){
                    bestSemiBlueVC = vc;
                    bestDepth = vc.depth;
                }
            }
        }
        bestDepth = 1000;
        for (VirtualConnection vc : board.getRedVCs()){
            if (board.movesOnBothRedEdges(vc.x, vc.y)){
                if (bestDepth > vc.depth){
                    bestRedVC = vc;
                    bestDepth = vc.depth;
                }
            }
        }
        bestDepth = 1000;
        for (VirtualConnection vc : board.getRedSemiVCs()){
            if (board.movesOnBothRedEdges(vc.x, vc.y)){
                if (bestDepth > vc.depth){
                    bestSemiRedVC = vc;
                    bestDepth = vc.depth;
                }
            }
        }
    }

    public void findConnections(){
        SetHolder setHolder = new SetHolder();
        int[] tempArr = board.HProcess(setHolder);
        findEndVCs();
    }

    public SandboxPosition(ConnectionPosition other) {
        super(other);
    }

    public SandboxPosition(Board boardObj) {
        super(boardObj);
    }
}
