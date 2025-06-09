package com.hex.algorithms.minimax;
import com.hex.components.Board;
import java.util.*;

public class Connections extends Board {

    private int[][] elecBoard;
    private int elecRows;
    private int elecCols;
    ArrayList<VirtualConnection> blueVCs;
    ArrayList<VirtualConnection> redVCs;
    ArrayList<VirtualConnection> blueSemiVCs;
    ArrayList<VirtualConnection> redSemiVCs;
    ArrayList<Move> emptyCells;
    ArrayList<Move> blueCells;
    ArrayList<Move> redCells;
    ArrayList<VirtualConnection> blueSemiWinConnections;
    ArrayList<VirtualConnection> blueWinConnections;
    ArrayList<VirtualConnection> redSemiWinConnections;
    ArrayList<VirtualConnection> redWinConnections;
    VirtualConnection bestBlueVC;
    VirtualConnection bestBlueSemiVC;
    VirtualConnection bestRedVC;
    VirtualConnection bestRedSemiVC;

    static enum Colors {
        RED(2),
        BLUE(1);

        private final int value;

        Colors(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    private void addToAdj(VirtualConnection vc, int type, int color) { // VC = 0, Semi = 1
        if (color == Colors.BLUE.getValue()) {
            if (movesOnBothBlueEdges(vc.x, vc.y)) {
                if (type == 0) {
                    blueWinConnections.add(vc);
                } else {
                    blueSemiWinConnections.add(vc);
                }
            }
        }
        if (color == Colors.RED.getValue()) {
            if (movesOnBothRedEdges(vc.x, vc.y)) {
                if (type == 0) {
                    redWinConnections.add(vc);
                } else {
                    redSemiWinConnections.add(vc);
                }
            }
        }
    }

    private boolean movesOnBothBlueEdges(Move move1, Move move2) {
        return (move1.x == 0 && move2.x == elecCols - 1)
                || (move2.x == 0 && move1.x == elecCols - 1);
    }

    private boolean movesOnBothRedEdges(Move move1, Move move2) {
        return (move1.y == 0 && move2.y == elecRows - 1)
                || (move2.y == 0 && move1.y == elecRows - 1);
    }

    private boolean toAdd(ArrayList<VirtualConnection> toAddList, ArrayList<VirtualConnection> parentList, int color, int type) {
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

    public boolean applyIDEKRule(ArrayList<VirtualConnection> semiVCs, int color) { //Måske en regel
        ArrayList<VirtualConnection> toAddList = new ArrayList<>();
        ArrayList<VirtualConnection> toAddSemiList = new ArrayList<>();
        Move[] connection;
        HashSet<Move> combinedCarrier;

        for (int i = 0; i < semiVCs.size(); i++) {
            VirtualConnection vc1 = semiVCs.get(i);
            if (vc1.depth != 1){
                continue;
            }

            for (int j = 0; j < semiVCs.size(); j++) {
                VirtualConnection vc2 = semiVCs.get(j);
                if (vc1 == vc2){
                    continue;
                }

                connection = vc1.getConnectingEnd(vc2);
                if (connection != null && (!vc1.endIsCarrier(vc2) && !vc2.endIsCarrier(vc1)) ) {
                    int cell = elecBoard[connection[0].x][connection[0].y];

                    combinedCarrier = new HashSet<>(vc1.carrier);
                    combinedCarrier.addAll(vc2.carrier);

                    VirtualConnection currVC = new VirtualConnection(connection[1], connection[2], combinedCarrier, vc2.depth);
                    if (cell == 0){
                        combinedCarrier.add(connection[0]);
                        if (semiVCs.contains(currVC)){ continue; }
                        toAddSemiList.add(currVC);
                    }
                }
            }
        }
        checkRedundancies(toAddSemiList);
        boolean a = toAdd(toAddSemiList, semiVCs, color, 1);

        return a;
    }

    private void checkRedundancies(List<VirtualConnection> vcList) {
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

    private boolean bothBlueMovesOnSameEdge(VirtualConnection vc) {
        return (vc.x.x == 0 && vc.y.x == 0) || (vc.x.x == elecCols - 1 && vc.y.x == elecCols - 1);
    }

    private boolean bothRedMovesOnSameEdge(VirtualConnection vc) {
        return (vc.x.y == 0 && vc.y.y == 0) || (vc.x.y == elecRows - 1 && vc.y.y == elecRows - 1);
    }

    private void cleanConnections(){
        blueVCs.removeIf(this::bothBlueMovesOnSameEdge);
        redVCs.removeIf(this::bothRedMovesOnSameEdge);
    }

    public void HProcess() {
        boolean a = applyAndRule(blueSemiVCs, blueVCs, Colors.BLUE.getValue());
        boolean b = applyAndRule(redSemiVCs, redVCs, Colors.RED.getValue());
        boolean c = false, d = false, e = false, f = false;

        boolean changed = (a || b);

        while (changed) {
            a = applyAndRule(blueSemiVCs, blueVCs, Colors.BLUE.getValue());
            b = applyAndRule(redSemiVCs, redVCs, Colors.RED.getValue());
            c = applyOrRule(blueSemiVCs, blueVCs, Colors.BLUE.getValue());
            d = applyOrRule(redSemiVCs, redVCs, Colors.RED.getValue());
            e = applyIDEKRule(blueSemiVCs, Colors.BLUE.getValue());
            f = applyIDEKRule(redSemiVCs, Colors.RED.getValue());
            changed = (a || b || c || d || e || f);
        }

        findAllEndVCs();

        if (!blueWinConnections.isEmpty()) {
            bestBlueVC = blueWinConnections.stream().min(Comparator.naturalOrder()).orElse(null);
        } if (!blueSemiWinConnections.isEmpty()){
            bestBlueSemiVC = blueSemiWinConnections.stream().min(Comparator.naturalOrder()).orElse(null);
        } if (!redWinConnections.isEmpty()){
            bestRedVC = redWinConnections.stream().min(Comparator.naturalOrder()).orElse(null);
        } if (!redSemiWinConnections.isEmpty()){
            bestRedSemiVC = redSemiWinConnections.stream().min(Comparator.naturalOrder()).orElse(null);
        }
    }

    private void findEndVCsFromList(ArrayList<VirtualConnection> VCs, int type, int color){
        Iterator<VirtualConnection> iterator = VCs.iterator();
        VirtualConnection vc;
        while (iterator.hasNext()) {
            vc = iterator.next();
            addToAdj(vc, type, color);
        }
    }

    private void findAllEndVCs(){
        findEndVCsFromList(blueVCs, 0, Colors.BLUE.getValue());
        findEndVCsFromList(blueSemiVCs, 1, Colors.BLUE.getValue());
        findEndVCsFromList(redVCs, 0, Colors.RED.getValue());
        findEndVCsFromList(redSemiVCs, 1, Colors.RED.getValue());
    }

    @Override
    public void setPiece(int x, int y, int player)  {
        board[x][y] = player;
        elecBoard[x + 1][y + 1] = player;
        Move setMove = new Move(x + 1, y + 1);
        emptyCells.remove(setMove);

        if(player == Colors.BLUE.getValue()){
            blueCells.add(setMove);
            findBaseVCs(setMove, blueSemiVCs, blueVCs, Colors.BLUE.getValue());
        } else {
            redCells.add(setMove);
            findBaseVCs(setMove, redSemiVCs, redVCs, Colors.RED.getValue());
        }
    }

    private void initializer(){
        blueSemiVCs = new ArrayList<>();
        blueVCs = new ArrayList<>();
        redSemiVCs = new ArrayList<>();
        redVCs = new ArrayList<>();
        blueSemiWinConnections = new ArrayList<>();
        blueWinConnections = new ArrayList<>();
        redSemiWinConnections = new ArrayList<>();
        redWinConnections = new ArrayList<>();
    }

    public Connections(Board board){
        super(board);
        emptyCells = new ArrayList<>(rows*cols);
        blueCells = new ArrayList<>(10);
        redCells = new ArrayList<>(10);
        elecCols = cols + 2;
        elecRows = rows + 2;
        elecBoard = new int[elecCols][elecRows];
        for (int y = 1; y < elecRows - 1; y++){
            elecBoard[0][y] = 1;
            blueCells.add(new Move(0, y));
            elecBoard[elecCols - 1][y] = 1;
            blueCells.add(new Move(elecCols - 1, y));
        }
        for (int x = 1; x < elecCols - 1; x++){
            elecBoard[x][0] = 2;
            redCells.add(new Move(x, 0));
            elecBoard[x][elecRows - 1] = 2;
            redCells.add(new Move(x, elecRows - 1));
        }
        for (int x = 0; x < cols; x++){
            for (int y = 0; y < rows; y++){
                int cell = this.board[x][y];
                elecBoard[x + 1][y + 1] = cell;
                if (cell == 0){
                    emptyCells.add(new Move(x+1, y+1));
                }
                else if (cell == Colors.BLUE.getValue()){
                    blueCells.add(new Move(x+1, y+1));
                }
                else if (cell == Colors.RED.getValue()){
                    redCells.add(new Move(x+1, y+1));
                }
            }
        }
        initializer();
        for (Move move : blueCells){
            findBaseVCs(move, blueSemiVCs, blueVCs, Colors.BLUE.getValue());
        }
        for (Move move : redCells){
            findBaseVCs(move, redSemiVCs, redVCs, Colors.RED.getValue());
        }
        cleanConnections();
    }

    public Connections(Connections other){
        super(other.getRows(), other.getCols(), other.getGameState());

        for (int i = 0; i < other.getRows(); i++) {
            System.arraycopy(other.getBoard()[i], 0, this.getBoard()[i], 0, other.getCols());
        }

        elecCols = cols + 2;
        elecRows = rows + 2;
        elecBoard = new int[elecCols][elecRows];

        for (int i = 0; i < other.elecCols; i++) {
            System.arraycopy(other.elecBoard[i], 0, this.elecBoard[i], 0, other.elecCols);
        }

        emptyCells = new ArrayList<>(other.emptyCells);
        redCells = new ArrayList<>(other.redCells);
        blueCells = new ArrayList<>(other.blueCells);
        blueVCs = new ArrayList<>(other.blueVCs);
        blueSemiVCs = new ArrayList<>(other.blueSemiVCs);
        redVCs = new ArrayList<>(other.redVCs);
        redSemiVCs = new ArrayList<>(other.redSemiVCs);

        blueWinConnections = new ArrayList<>(3);
        blueSemiWinConnections = new ArrayList<>(3);
        redWinConnections = new ArrayList<>(3);
        redSemiWinConnections = new ArrayList<>(3);


    }
}