package com.hex.algorithms.minimax.connectionsHelpers;

import com.hex.GameState;
import com.hex.algorithms.minimax.Move;
import com.hex.algorithms.minimax.VirtualConnection;
import com.hex.components.Board;

import java.util.*;

// Christian
public class SimpleConnectionsLogic extends SimpleFuncs {

    /**
     * Checks if the new VCs should be added, by checking if they are/aren't a subset of previous VCs
     * @param toAddList hashlist that is being checked if should be added
     * @param newCheckParentList the list that created toAddList by applying rules
     * @param parentList the old list of VCs
     * @param color of the VCs being added
     * @param type 0 for VC, 1 for virtual semi connections
     */
    protected HashSet<VirtualConnection> toAdd(HashSet<VirtualConnection> toAddList, HashSet<VirtualConnection> newCheckParentList, HashSet<VirtualConnection> parentList, int color, int type) {
        HashSet<VirtualConnection> hasBeenChecked = new HashSet<>();
        HashSet<VirtualConnection> toRemoveFromToAdd = new HashSet<>();
        HashSet<VirtualConnection> toRemoveFromParents = new HashSet<>();

        List<VirtualConnection> allParents = new ArrayList<>(parentList);
        allParents.addAll(newCheckParentList);


        Map<Integer, List<VirtualConnection>> toAddByEnds = new HashMap<>();
        for (VirtualConnection vc : toAddList) {
            int endsKey = vc.getMovesCode();
            toAddByEnds.computeIfAbsent(endsKey, k -> new ArrayList<>()).add(vc);
        }

        Map<Integer, List<VirtualConnection>> parentsByEnds = new HashMap<>();
        for (VirtualConnection vc : allParents) {
            int endsKey = vc.getMovesCode();
            parentsByEnds.computeIfAbsent(endsKey, k -> new ArrayList<>()).add(vc);
        }

        for (Integer endsKey : toAddByEnds.keySet()) {
            List<VirtualConnection> toAddGroup = toAddByEnds.get(endsKey);
            List<VirtualConnection> parentGroup = parentsByEnds.getOrDefault(endsKey, Collections.emptyList());

            for (VirtualConnection vcToBeAdded : toAddGroup) {
                boolean add = true;
                for (VirtualConnection vcParent : parentGroup) {
                    if (vcParent.isSubset(vcToBeAdded)) {
                        add = false;
                        toRemoveFromToAdd.add(vcToBeAdded);
                        break;
                    } else if (vcToBeAdded.isSubset(vcParent)) {
                        toRemoveFromParents.add(vcParent);
                    }
                }
                if (add) {
                    hasBeenChecked.add(vcToBeAdded);
                }
            }
        }
        // Batch updates
        toAddList.removeAll(toRemoveFromToAdd);
        parentList.removeAll(toRemoveFromParents);
        newCheckParentList.removeAll(toRemoveFromParents);

        return hasBeenChecked;
    }


    /**
     * Checks if the lists has VCs that are subsets of other VCs within the same list and removes the non minimal ones
     * @param vcList the list of VCs
     */
    protected void checkRedundancies(Set<VirtualConnection> vcList) {
        Map<Integer, List<VirtualConnection>> groupsByEnds = new HashMap<>();
        for (VirtualConnection vc : vcList) {
            int endsKey = vc.getMovesCode();
            groupsByEnds.computeIfAbsent(endsKey, k -> new ArrayList<>()).add(vc);
        }

        Set<VirtualConnection> toRemove = new HashSet<>();
        for (List<VirtualConnection> group : groupsByEnds.values()) {
            group.sort(Comparator.comparingInt(vc -> vc.carrier.size()));
            for (int i = 0; i < group.size(); i++) {
                VirtualConnection vc1 = group.get(i);
                if (toRemove.contains(vc1)) continue;
                for (int j = i + 1; j < group.size(); j++) {
                    VirtualConnection vc2 = group.get(j);
                    if (toRemove.contains(vc2)) continue;
                    if (vc1.isSubset(vc2)) {
                        toRemove.add(vc2);
                    }
                }
            }
        }

        // Batch removal
        vcList.removeAll(toRemove);
    }

    // Delta bridge cell location, and two array index for cellVals to check for the corresponding bridge
    static Move[][] deltaMoves = {
            { new Move(1, -2),  new Move(3, 4) },
            { new Move(2, -1),  new Move(4, 0) },
            { new Move(1, 1),   new Move(0, 2) },
            { new Move(-1, 2),  new Move(2, 5) },
            { new Move(-2, 1),  new Move(5, 1) },
            { new Move(-1, -1), new Move(1, 3) }
    };
    // Cells in carrier set
    static Move[][] brHelper = {
            { new Move(0, -1), new Move(1, -1) },
            { new Move(1, -1), new Move(1, 0) },
            { new Move(1, 0), new Move(0, 1) },
            { new Move(0, 1), new Move(-1, 1) },
            { new Move(-1, 1), new Move(-1, 0) },
            { new Move(-1, 0), new Move(0, -1) }
    };

    /**
     * Finds all bridges of move, based on values of cells around it
     * @param VCs Set of VC that bridges are added to
     * @param semiVCs Set of Semi-VC that bridges are added to
     * @param move that is being checked for bridges
     * @param cellVals values of cells around the move
     * @param color of cell that was placed aka. move
     */
    private void findBridges(HashSet<VirtualConnection> VCs, HashSet<VirtualConnection> semiVCs, Move move, int[] cellVals, int color) {// Word
        for (int i = 0; i < deltaMoves.length; i++){
            if (cellVals[deltaMoves[i][1].x] == 0 && cellVals[deltaMoves[i][1].y] == 0) {
                Move tempMove = move.addGet(deltaMoves[i][0].x, deltaMoves[i][0].y);
                if (moveWithinBoard(tempMove)) {
                    int cell = elecBoard[tempMove.x][tempMove.y];
                    if (cell == color) {
                        HashSet<Move> tempCarrier = new HashSet<>(2);
                        tempCarrier.add(new Move(move.x + brHelper[i][0].x, move.y + brHelper[i][0].y));
                        tempCarrier.add(new Move(move.x + brHelper[i][1].x, move.y + brHelper[i][1].y));
                        VirtualConnection tempVC = new VirtualConnection(move, tempMove, tempCarrier, 2);
                        VCs.add(tempVC);
                    } if (cell == 0) {
                        HashSet<Move> tempCarrier = new HashSet<>(2);
                        tempCarrier.add(new Move(move.x + brHelper[i][0].x, move.y + brHelper[i][0].y));
                        tempCarrier.add(new Move(move.x + brHelper[i][1].x, move.y + brHelper[i][1].y));
                        VirtualConnection tempVC = new VirtualConnection(move, tempMove, tempCarrier, 3, tempMove);
                        semiVCs.add(tempVC);
                    }
                }
            }
        }
    }

    /**
     * Finds values of cells around placed move, and checks for bridges.
     * @param move coordinates of placed piece
     * @param semiVCs set of semiVCs to add into
     * @param VCs set of VCs to add into
     * @param color of cell that was placed aka. move
     */
    public void findBaseVCs(Move move, HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> VCs, int color) {
        int[] cellVals = new int[6];
        for (int i = 0; i < directions.length; i++) {
            int[] dir = directions[i];
            int tempx = move.x + dir[0];
            int tempy = move.y + dir[1];
            Move tempMove = move.addGet(dir[0], dir[1]);

            if (!moveWithinBoard(tempMove)) {
                continue;
            }

            Set<Move> carrier = new HashSet<>();
            int cell = elecBoard[tempx][tempy];
            cellVals[i] = cell;
            VirtualConnection currVC = new VirtualConnection(move, tempMove, carrier, 0);

            if (cell == 0) {
                currVC.depth = 1;
                currVC.criticalCell = tempMove;
                semiVCs.add(currVC);
            }
            if (cell == color) {
                VCs.add(currVC);
            }
        }
        findBridges(VCs, semiVCs, move, cellVals, color);
    }

    /**
     * Finds Semi-VCs with same ends and returns it
     * @param newCheckSemiVCs the semi-vcs being checked
     * @param semiVCs checks if semivcs with same ends are found in old vcs.
     * @return list of (sets of vcs with same ends)
     */
    private ArrayList<HashSet<VirtualConnection>> equalEndsOrRules(HashSet<VirtualConnection> newCheckSemiVCs, HashSet<VirtualConnection> semiVCs) {
        ArrayList<HashSet<VirtualConnection>> orRulePrelim = new ArrayList<>();

        List<VirtualConnection> allVCs = new ArrayList<>(newCheckSemiVCs);
        allVCs.addAll(semiVCs);
        Map<Integer, HashSet<VirtualConnection>> groupsByEnds = new HashMap<>();
        for (VirtualConnection vc : allVCs) {
            int endsKey = vc.getMovesCode();
            groupsByEnds.computeIfAbsent(endsKey, k -> new HashSet<>()).add(vc);
        }
        for (HashSet<VirtualConnection> group : groupsByEnds.values()) {
            if (group.size() > 1) {
                checkRedundancies(group);
                orRulePrelim.add(group);
            }
        }
        return orRulePrelim;
    }

    public record CarrierDepthPair(HashSet<Move> carrier, Integer depth, Move x, Move y) {}

    /**
     * Finds second highest depth among cells with semi unique carrier sets
     * @param vcList list of cells with same end
     * @return second highest depth with the end values
     */
    private CarrierDepthPair orRuleHelper(HashSet<VirtualConnection> vcList){
        List<Integer> sortingList = new ArrayList<>();
        HashSet<Move> currCarrier = new HashSet<>();
        Move x = null;
        Move y = null;
        for (VirtualConnection vc1 : vcList){
            for (VirtualConnection vc2 : vcList) {
                if (!vc1.sameCriticalCell(vc2) && (!vc1.crititcalCellIsImportant(vc2) || !vc2.crititcalCellIsImportant(vc1))) { sortingList.add(Math.max(vc1.depth, vc2.depth)); currCarrier.addAll(vc1.carrier); x = vc1.x; y = vc1.y; }
            }
        }
        Collections.sort(sortingList);
        if (sortingList.isEmpty()) return null;
        return new CarrierDepthPair(currCarrier, sortingList.get(0), x, y);
    }

    /**
     * Applies Or Rule
     * @param newCheckSemiVCs new semi VCs to be checked if rule applies among themselves and parent
     * @param semiVCs old semi vcs that newer checks with to applu Or Rule
     * @return set of new VCs produced by Or Rule.
     */
    public HashSet<VirtualConnection> applyOrRule(HashSet<VirtualConnection> newCheckSemiVCs, HashSet<VirtualConnection> semiVCs) {
        VirtualConnection vc = new VirtualConnection(null, null, new HashSet<>(0), -1); // Så den builder. List kan ikke være tom
        ArrayList<HashSet<VirtualConnection>> orRulePrelim = equalEndsOrRules(newCheckSemiVCs, semiVCs);
        VirtualConnection tempVC;
        HashSet<VirtualConnection> toAdd = new HashSet<>();

        for (HashSet<VirtualConnection> list : orRulePrelim){
            CarrierDepthPair carrierDepthPair = orRuleHelper(list);
            if (carrierDepthPair == null) continue;
            tempVC = new VirtualConnection(carrierDepthPair.x, carrierDepthPair.y, carrierDepthPair.carrier, carrierDepthPair.depth);
            toAdd.add(tempVC);
        }
        return toAdd;
    }

    /**
     * Applies and rule on new VCs
     * @param checkNewVCs the new vcs that applies and rule on eachother and old vcs
     * @param VCs old vcs
     * @param color of vcs
     * @return set of vcs created by and rule
     */
    public HashSet<VirtualConnection> applyAndRule(HashSet<VirtualConnection> checkNewVCs, HashSet<VirtualConnection> VCs, int color) {
        HashSet<VirtualConnection> toAddList = new HashSet<>();
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
                    if (cell == color) {
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
                    if (cell == color) {
                        toAddList.add(currVC);
                    }
                }
            }
        }
        checkRedundancies(toAddList);
        return toAdd(toAddList, checkNewVCs, VCs, color, 0);
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