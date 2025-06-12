package com.hex.algorithms.minimax.connectionsHelpers;

import com.hex.GameState;
import com.hex.algorithms.minimax.Move;
import com.hex.algorithms.minimax.VirtualConnection;
import com.hex.components.Board;

import java.util.*;

public class SimpleConnectionsLogic extends SimpleFuncs {

    protected boolean toAdd(ArrayList<VirtualConnection> toAddList, ArrayList<VirtualConnection> parentList, int color, int type) {
        ArrayList<VirtualConnection> hasBeenChecked = new ArrayList<>();
        boolean somethingChanged = false;
        VirtualConnection vcParent;
        VirtualConnection vcToBeAdded;


        Iterator<VirtualConnection> toAddIterator = toAddList.iterator();
        boolean add = false;
        while (toAddIterator.hasNext()) {
            vcToBeAdded = toAddIterator.next();
            add = true;

            Iterator<VirtualConnection> parentIterator = parentList.iterator();
            while (parentIterator.hasNext()) {
                vcParent = parentIterator.next();

                if (vcParent.equals(vcToBeAdded) || vcParent.isSubset(vcToBeAdded)) {
                    add = false;
                    toAddIterator.remove();
                    break;
                } else if (vcToBeAdded.isSubset(vcParent)) {
                    parentIterator.remove();
                }
            }
            if (add) {
                hasBeenChecked.add(vcToBeAdded);
            }
        }
        checkRedundancies(hasBeenChecked);
        for (VirtualConnection elem : hasBeenChecked) {
            parentList.add(elem);
            somethingChanged = true;
        }
        return somethingChanged;
    }

    protected void checkRedundancies(List<VirtualConnection> vcList) {
        Set<Integer> indicesToRemove = new HashSet<>();

        for (int i = 0; i < vcList.size(); i++) {
            if (indicesToRemove.contains(i)) continue;

            VirtualConnection vc1 = vcList.get(i);

            for (int j = i + 1; j < vcList.size(); j++) {
                if (indicesToRemove.contains(j)) continue;

                VirtualConnection vc2 = vcList.get(j);

                if (vc2.isSubset(vc1) ) {
                    indicesToRemove.add(i);
                } else if (vc1.isSubset(vc2) || vc2.equals(vc1)) {
                    indicesToRemove.add(j);
                    break;
                }
            }
        }
        List<VirtualConnection> filtered = new ArrayList<>();
        for (int i = 0; i < vcList.size(); i++) {
            if (!indicesToRemove.contains(i)) {
                filtered.add(vcList.get(i));
            }
        }
        vcList.clear();
        vcList.addAll(filtered);
    }

    static Move[][] deltaMoves = {
            { new Move(1, -2),  new Move(3, 4) },
            { new Move(2, -1),  new Move(4, 0) },
            { new Move(1, 1),   new Move(0, 2) },
            { new Move(-1, 2),  new Move(2, 5) },
            { new Move(-2, 1),  new Move(5, 1) },
            { new Move(-1, -1), new Move(1, 3) }
    };
    static Move[][] brHelper = {
            { new Move(0, -1), new Move(1, -1) },
            { new Move(1, -1), new Move(1, 0) },
            { new Move(1, 0), new Move(0, 1) },
            { new Move(0, 1), new Move(-1, 1) },
            { new Move(-1, 1), new Move(-1, 0) },
            { new Move(-1, 0), new Move(0, -1) }
    };


    private void findBridges(ArrayList<VirtualConnection> VCs, ArrayList<VirtualConnection> semiVCs, Move move, int[] cellVals, int color) {// Word
        for (int i = 0; i < deltaMoves.length; i++){
            if (cellVals[deltaMoves[i][1].x] == 0 && cellVals[deltaMoves[i][1].y] == 0) {
                int tempx = move.x + deltaMoves[i][0].x;
                int tempy = move.y + deltaMoves[i][0].y;

                if (!(tempy < 0 || tempy > elecRows-1 || tempx < 0 || tempx > elecCols-1 || (tempx == 0 && (tempy == 0 || tempy == elecRows - 1)) || (tempx == elecCols - 1 && (tempy == 0 || tempy == elecRows - 1)))) {
                    int cell = elecBoard[tempx][tempy];
                    if (cell == color) {
                        HashSet<Move> tempCarrier = new HashSet<>(2);
                        tempCarrier.add(new Move(move.x + brHelper[i][0].x, move.y + brHelper[i][0].y));
                        tempCarrier.add(new Move(move.x + brHelper[i][1].x, move.y + brHelper[i][1].y));
                        VirtualConnection tempVC = new VirtualConnection(move, new Move(tempx, tempy), tempCarrier, 2);
                        if (!VCs.contains(tempVC)){
                            VCs.add(tempVC);
                        }
                    } if (cell == 0) {
                        HashSet<Move> tempCarrier = new HashSet<>(2);
                        tempCarrier.add(new Move(move.x + brHelper[i][0].x, move.y + brHelper[i][0].y));
                        tempCarrier.add(new Move(move.x + brHelper[i][1].x, move.y + brHelper[i][1].y));
                        VirtualConnection tempVC = new VirtualConnection(move, new Move(tempx, tempy), tempCarrier, 3);
                        if (!semiVCs.contains(tempVC)){
                            semiVCs.add(tempVC);
                        }
                    }
                }
            }
        }
    }

    public void findBaseVCs(Move move, ArrayList<VirtualConnection> semiVCs, ArrayList<VirtualConnection> VCs, int color) {
        int[] cellVals = new int[6];
        for (int i = 0; i < directions.length; i++) {
            int[] dir = directions[i];
            int tempx = move.x + dir[0];
            int tempy = move.y + dir[1];

            if (tempy == -1 || tempy == elecRows || tempx == -1 || tempx == elecCols || (tempx == 0 && (tempy == 0 || tempy == elecRows - 1)) || (tempx == elecCols - 1 && (tempy == 0 || tempy == elecRows - 1))) {
                continue;
            }
            Move y = new Move(tempx, tempy);
            Set<Move> carrier = new HashSet<>();

            int cell = elecBoard[tempx][tempy];

            cellVals[i] = cell;

            VirtualConnection currVC = new VirtualConnection(move, y, carrier, 0);

            if (cell == 0) {
                if (!semiVCs.contains(currVC)) {
                    currVC.depth = 1;
                    semiVCs.add(currVC);
                }
            }
            if (cell == color && !VCs.contains(currVC)) {
                VCs.add(currVC);
            }
        }
        findBridges(VCs, semiVCs, move, cellVals, color);
    }

    private ArrayList<ArrayList<VirtualConnection>> equalEndsOrRules(ArrayList<VirtualConnection> semiVCs) {
        ArrayList<ArrayList<VirtualConnection>> orRulePrelim = new ArrayList<>();
        HashSet<Integer> alreadyAdded = new HashSet<>();
        ArrayList<VirtualConnection> currList;


        for (int i = 0; i < semiVCs.size(); i++) {
            VirtualConnection vc1 = semiVCs.get(i);
            if (alreadyAdded.contains(i)){
                continue;
            }
            currList = new ArrayList<>();
            currList.add(vc1);
            for (int j = i + 1; j < semiVCs.size(); j++) {
                if (alreadyAdded.contains(j)){
                    continue;
                }
                VirtualConnection vc2 = semiVCs.get(j);

                if (vc1 != vc2 && vc1.equalEnds(vc2)) {
                    alreadyAdded.add(i);
                    alreadyAdded.add(j);
                    currList.add(vc2);
                }
            }
            checkRedundancies(currList);
            if (currList.size() > 1){
                orRulePrelim.add(currList);
            }
        }
        return orRulePrelim;
    }

    public boolean applyOrRule(ArrayList<VirtualConnection> semiVCs, ArrayList<VirtualConnection> VCs, int color) {
        int maxDepth = -1;
        HashSet<Move> currCarrier;
        Move x;
        Move y;
        VirtualConnection vc = new VirtualConnection(null, null, new HashSet<>(0), -1); // Så den builder. List kan ikke være tom
        ArrayList<ArrayList<VirtualConnection>> orRulePrelim = equalEndsOrRules(semiVCs);
        VirtualConnection tempVC;

        boolean changed = false;
        for (ArrayList<VirtualConnection> list : orRulePrelim){
            currCarrier = new HashSet<>();
            for (int i = 0; i < list.size(); i++){
                vc = list.get(i);
                currCarrier.addAll(vc.carrier);
                if (maxDepth < vc.depth){
                    maxDepth = vc.depth;
                }
            }
            tempVC = new VirtualConnection(vc.x, vc.y, currCarrier, maxDepth + 2);
            if (!VCs.contains(tempVC)){
                VCs.add(tempVC);
                changed = true;
            }
        }
        return changed;
    }

    public boolean applyAndRule(ArrayList<VirtualConnection> semiVCs, ArrayList<VirtualConnection> VCs, int color) {
        ArrayList<VirtualConnection> toAddList = new ArrayList<>();
        ArrayList<VirtualConnection> toAddSemiList = new ArrayList<>();

        for (int i = 0; i < VCs.size(); i++) {
            VirtualConnection vc1 = VCs.get(i);

            for (int j = i + 1; j < VCs.size(); j++) {
                VirtualConnection vc2 = VCs.get(j);

                Move[] connection = vc1.getConnectingEnd(vc2);
                if (connection != null && (!vc1.endIsCarrier(vc2) && !vc2.endIsCarrier(vc1)) ) {
                    int cell = elecBoard[connection[0].x][connection[0].y];

                    HashSet<Move> combinedCarrier = new HashSet<>(vc1.carrier);
                    combinedCarrier.addAll(vc2.carrier);


                    VirtualConnection currVC = new VirtualConnection(connection[1], connection[2], combinedCarrier, vc1.depth + vc2.depth);
                    if (cell == 0){
                        combinedCarrier.add(connection[0]);
                        if (semiVCs.contains(currVC)){ continue; }
                        toAddSemiList.add(currVC);

                    } else if (cell == color) {
                        if (VCs.contains(currVC)){ continue; }
                        toAddList.add(currVC);
                    }
                }
            }
        }
        checkRedundancies(toAddSemiList);
        boolean a = toAdd(toAddSemiList, semiVCs, color, 1);

        checkRedundancies(toAddList);
        boolean b = toAdd(toAddList, VCs, color, 0);

        return b || a;
    }


    public SimpleConnectionsLogic(int rows, int cols) {
        super(rows, cols);
    }

    public SimpleConnectionsLogic(int rows, int cols, GameState gameState) {
        super(rows, cols, gameState);
    }

    public SimpleConnectionsLogic(Board other) {
        super(other);
    }
}
