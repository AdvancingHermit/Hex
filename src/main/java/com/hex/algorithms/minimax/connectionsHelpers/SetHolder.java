package com.hex.algorithms.minimax.connectionsHelpers;

import com.hex.algorithms.minimax.VirtualConnection;

import java.util.HashSet;

public class SetHolder {

    public HashSet<VirtualConnection> newBlueVCs;
    public HashSet<VirtualConnection> newBlueSemiVCs;
    public HashSet<VirtualConnection> newRedVCs;
    public HashSet<VirtualConnection> newRedSemiVCs;
    public HashSet<VirtualConnection> checkNewBlueVCs;
    public HashSet<VirtualConnection> checkNewBlueSemiVCs;
    public HashSet<VirtualConnection> checkNewRedVCs;
    public HashSet<VirtualConnection> checkNewRedSemiVCs;



    public SetHolder(){
        newBlueVCs = new HashSet<>();
        newBlueSemiVCs = new HashSet<>();
        newRedVCs = new HashSet<>();
        newRedSemiVCs = new HashSet<>();
        checkNewBlueVCs = new HashSet<>();
        checkNewBlueSemiVCs = new HashSet<>();
        checkNewRedVCs = new HashSet<>();
        checkNewRedSemiVCs = new HashSet<>();
    }

    public void clearAll(){
        newBlueVCs.clear();
        newBlueSemiVCs.clear();
        newRedVCs.clear();
        newRedSemiVCs.clear();
        checkNewBlueVCs.clear();
        checkNewBlueSemiVCs.clear();
        checkNewRedVCs.clear();
        checkNewRedSemiVCs.clear();
    }

}
