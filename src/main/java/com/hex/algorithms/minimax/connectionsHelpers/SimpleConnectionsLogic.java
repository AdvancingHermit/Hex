package com.hex.algorithms.minimax.connectionsHelpers;

import com.hex.GameState;
import com.hex.algorithms.minimax.Move;
import com.hex.algorithms.minimax.VirtualConnection;
import com.hex.components.Board;

import java.util.*;

public class SimpleConnectionsLogic extends SimpleFuncs {

    protected HashSet<VirtualConnection> toAdd(HashSet<VirtualConnection> toAddList, HashSet<VirtualConnection> newCheckParentList, HashSet<VirtualConnection> parentList, int color, int type) {
        HashSet<VirtualConnection> hasBeenChecked = new HashSet<>();
        VirtualConnection vcParent;
        VirtualConnection vcToBeAdded;
        Iterator<VirtualConnection> parentIterator;

        Iterator<VirtualConnection> toAddIterator = toAddList.iterator();
        boolean add = false;
        while (toAddIterator.hasNext()) {
            vcToBeAdded = toAddIterator.next();
            add = true;

            parentIterator = parentList.iterator();
            while (parentIterator.hasNext()) {
                vcParent = parentIterator.next();

                if (vcParent.isSubset(vcToBeAdded)) {
                    add = false;
                    toAddIterator.remove();
                    break;
                } else if (vcToBeAdded.isSubset(vcParent)) {
                    parentIterator.remove();
                }
            }
            if (!add) { continue; }
            parentIterator = newCheckParentList.iterator();
            while (parentIterator.hasNext()) {
                vcParent = parentIterator.next();

                if (vcParent.isSubset(vcToBeAdded)) {
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
        return hasBeenChecked;
    }

    protected void checkRedundancies(Set<VirtualConnection> vcList) {
        List<VirtualConnection> snapshot = new ArrayList<>(vcList);
        for (VirtualConnection vc1 : snapshot) {
            for (VirtualConnection vc2 : snapshot) {
                if (vc1 == vc2) {
                    continue;
                }
                if (vc1.isSubset(vc2)) {
                    vcList.remove(vc1);
                    break;
                }
            }
        }
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


    private void findBridges(HashSet<VirtualConnection> VCs, HashSet<VirtualConnection> semiVCs, Move move, int[] cellVals, int color) {// Word
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
                        VCs.add(tempVC);
                    } if (cell == 0) {
                        HashSet<Move> tempCarrier = new HashSet<>(2);
                        tempCarrier.add(new Move(move.x + brHelper[i][0].x, move.y + brHelper[i][0].y));
                        tempCarrier.add(new Move(move.x + brHelper[i][1].x, move.y + brHelper[i][1].y));
                        VirtualConnection tempVC = new VirtualConnection(move, new Move(tempx, tempy), tempCarrier, 3, new Move(tempx, tempy));
                        semiVCs.add(tempVC);
                    }
                }
            }
        }
    }

    public void findBaseVCs(Move move, HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> VCs, int color) {
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
                currVC.depth = 1;
                currVC.criticalCell = y;
                semiVCs.add(currVC);
            }
            if (cell == color) {
                VCs.add(currVC);
            }
        }
        findBridges(VCs, semiVCs, move, cellVals, color);
    }

    private ArrayList<HashSet<VirtualConnection>> equalEndsOrRules(HashSet<VirtualConnection> newCheckSemiVCs, HashSet<VirtualConnection> semiVCs) {
        ArrayList<HashSet<VirtualConnection>> orRulePrelim = new ArrayList<>();
        HashSet<VirtualConnection> alreadyAdded = new HashSet<>();
        HashSet<VirtualConnection> currList;

        for (VirtualConnection vc1 : newCheckSemiVCs) {
            if (alreadyAdded.contains(vc1)){
                continue;
            }
            currList = new HashSet<>();
            currList.add(vc1);
            for (VirtualConnection vc2 : newCheckSemiVCs) {
                if (alreadyAdded.contains(vc2)){
                    continue;
                }
                if (vc1 != vc2 && vc1.equalEnds(vc2)) {
                    alreadyAdded.add(vc1);
                    alreadyAdded.add(vc2);
                    currList.add(vc2);
                }
            }
            for (VirtualConnection vc2 : semiVCs) {
                if (alreadyAdded.contains(vc2)){
                    continue;
                }
                if (vc1 != vc2 && vc1.equalEnds(vc2)) {
                    alreadyAdded.add(vc1);
                    alreadyAdded.add(vc2);
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

    public HashSet<VirtualConnection> applyOrRule(HashSet<VirtualConnection> newCheckSemiVCs, HashSet<VirtualConnection> semiVCs) {
        int maxDepth = -1;
        HashSet<Move> currCarrier;
        Move x;
        Move y;
        VirtualConnection vc = new VirtualConnection(null, null, new HashSet<>(0), -1); // Så den builder. List kan ikke være tom
        ArrayList<HashSet<VirtualConnection>> orRulePrelim = equalEndsOrRules(newCheckSemiVCs, semiVCs);
        VirtualConnection tempVC;
        HashSet<VirtualConnection> toAdd = new HashSet<>();

        for (HashSet<VirtualConnection> list : orRulePrelim){
            currCarrier = new HashSet<>();
            for (VirtualConnection vc_temp : list){
                vc = vc_temp;
                currCarrier.addAll(vc.carrier);
                if (maxDepth < vc.depth){
                    maxDepth = vc.depth;
                }
            }
            tempVC = new VirtualConnection(vc.x, vc.y, currCarrier, maxDepth + 2);
            toAdd.add(tempVC);
        }
        return toAdd;
    }

    private void semiAndRuleHelper(HashSet<VirtualConnection> toAdd, HashSet<VirtualConnection> checkNewVCs, HashSet<VirtualConnection> VCs){
        Iterator<VirtualConnection> iterator = toAdd.iterator();
        VirtualConnection toAddVC;
        while (iterator.hasNext()){
            toAddVC = iterator.next();
            for (VirtualConnection checkVC : VCs){
                if (toAddVC.isSubset(checkVC)){
                    iterator.remove();
                }
            }
            for (VirtualConnection checkVC : checkNewVCs){
                if (toAddVC.isSubset(checkVC)){
                    iterator.remove();
                }
            }
        }

    }

    public HashSet<VirtualConnection>[] applyAndRule(HashSet<VirtualConnection> checkNewSemiVCs, HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> checkNewVCs, HashSet<VirtualConnection> VCs, int color) {
        HashSet<VirtualConnection> toAddList = new HashSet<>();
        HashSet<VirtualConnection> toAddSemiList = new HashSet<>();
        HashSet<VirtualConnection>[] toReturn = new HashSet[2];

        for (VirtualConnection vc1 : checkNewVCs) {
            for (VirtualConnection vc2 : checkNewVCs) {
                if (vc1 == vc2) { continue; }
                Move[] connection = vc1.getConnectingEnd(vc2);
                if (connection != null && (!vc1.endIsCarrier(vc2) && !vc2.endIsCarrier(vc1)) ) {
                    int cell = elecBoard[connection[0].x][connection[0].y];

                    HashSet<Move> combinedCarrier = new HashSet<>(vc1.carrier);
                    combinedCarrier.addAll(vc2.carrier);


                    VirtualConnection currVC = new VirtualConnection(connection[1], connection[2], combinedCarrier, vc1.depth + vc2.depth);
                    if ( (bothBlueMovesOnSameEdge(currVC) && (color == Colors.BLUE.getValue())) || (bothRedMovesOnSameEdge(currVC) && (color == Colors.RED.getValue())) ) { continue; }
                    if (cell == 0){
                        combinedCarrier.add(connection[0]);
                        currVC.criticalCell = connection[0];
                        toAddSemiList.add(currVC);
                    } else if (cell == color) {
                        toAddList.add(currVC);
                    }
                }
            }
            for (VirtualConnection vc2 : VCs) {
                Move[] connection = vc1.getConnectingEnd(vc2);
                if (connection != null && (!vc1.endIsCarrier(vc2) && !vc2.endIsCarrier(vc1)) ) {
                    int cell = elecBoard[connection[0].x][connection[0].y];

                    HashSet<Move> combinedCarrier = new HashSet<>(vc1.carrier);
                    combinedCarrier.addAll(vc2.carrier);


                    VirtualConnection currVC = new VirtualConnection(connection[1], connection[2], combinedCarrier, vc1.depth + vc2.depth);
                    if ( (bothBlueMovesOnSameEdge(currVC) && (color == Colors.BLUE.getValue())) || (bothRedMovesOnSameEdge(currVC) && (color == Colors.RED.getValue())) ) { continue; }
                    if (cell == 0){
                        combinedCarrier.add(connection[0]);
                        currVC.criticalCell = connection[0];
                        toAddSemiList.add(currVC);
                    } else if (cell == color) {
                        toAddList.add(currVC);
                    }
                }
            }
        }
        checkRedundancies(toAddSemiList);
        semiAndRuleHelper(toAddSemiList, checkNewVCs, VCs);
        toReturn[1] = toAdd(toAddSemiList, checkNewSemiVCs, semiVCs, color, 1);

        checkRedundancies(toAddList);
        toReturn[0] = toAdd(toAddList, checkNewVCs, VCs, color, 0);
        return toReturn;
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