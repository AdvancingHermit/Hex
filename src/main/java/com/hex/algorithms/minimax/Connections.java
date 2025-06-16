package com.hex.algorithms.minimax;
import com.hex.algorithms.minimax.connectionsHelpers.SimpleConnectionsLogic;
import com.hex.components.Board;
import java.util.*;

public class Connections extends SimpleConnectionsLogic {

    HashSet<VirtualConnection> blueSemiWinConnections;
    HashSet<VirtualConnection> blueWinConnections;
    HashSet<VirtualConnection> redSemiWinConnections;
    HashSet<VirtualConnection> redWinConnections;
    VirtualConnection bestBlueVC;
    VirtualConnection bestBlueSemiVC;
    VirtualConnection bestRedVC;
    VirtualConnection bestRedSemiVC;


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

    private void IDEKHelper(HashSet<VirtualConnection> toAddSemiList, HashSet<VirtualConnection> semiVCs) {
        List<VirtualConnection> toRemove = new ArrayList<>();

        for (VirtualConnection toAdd : toAddSemiList) {
            if (toRemove.contains(toAdd)) { continue; }
            for (VirtualConnection checkVC : toAddSemiList) {
                if (toAdd == checkVC) {
                    continue;
                }
                if (toAdd.depth == checkVC.depth &&
                        toAdd.carrier.size() == checkVC.carrier.size() &&
                        toAdd.x.equals(checkVC.y) && toAdd.y.equals(checkVC.x)) {
                    toRemove.add(checkVC);
                }
            }
            for (VirtualConnection checkVC : semiVCs) {
                if (toAdd.depth == checkVC.depth &&
                        toAdd.carrier.size() == checkVC.carrier.size() &&
                        toAdd.x.equals(checkVC.y) && toAdd.y.equals(checkVC.x)) {
                    toRemove.add(toAdd);
                    break;
                }
            }
        }
        toRemove.forEach(toAddSemiList::remove);
    }



    private void applyIDEKRule(HashSet<VirtualConnection> semiVCs, int color) { //Måske en regel
        HashSet<VirtualConnection> toAddSemiList = new HashSet<>();
        Move[] connection;
        HashSet<Move> combinedCarrier;

        for (VirtualConnection vc1 : semiVCs) {
            if (vc1.depth != 1 && !vc1.carrier.isEmpty()){
                continue;
            }
            for (VirtualConnection vc2 : semiVCs) {
                if (vc1 == vc2){
                    continue;
                }
                connection = vc1.getConnectingEnd(vc2);
                if (connection != null && (!vc1.endIsCarrier(vc2) && !vc2.endIsCarrier(vc1)) ) {

                    if (checkIfNeighbor(connection[1], connection[2])) { continue; }
                    int cell = elecBoard[connection[0].x][connection[0].y];

                    combinedCarrier = new HashSet<>(vc1.carrier);
                    combinedCarrier.addAll(vc2.carrier);

                    VirtualConnection currVC = new VirtualConnection(connection[1], connection[2], combinedCarrier, vc2.depth);
                    if (cell == 0){
                        combinedCarrier.add(connection[0]);
                        toAddSemiList.add(currVC);
                    }
                }
            }
        }
        checkRedundancies(toAddSemiList);
        IDEKHelper(toAddSemiList, semiVCs);
        toAdd(toAddSemiList, semiVCs, color, 1);
    }

    public void applyLastRule(HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> VCs, int color) { //Måske en regel
        HashSet<VirtualConnection> toAddSemiList = new HashSet<>();
        Move[] connection;
        HashSet<Move> combinedCarrier;

        for (VirtualConnection vc1 : VCs) {
            for (VirtualConnection vc2 : semiVCs) {
                connection = vc1.getConnectingEnd(vc2);
                if (connection != null) {
                    combinedCarrier = new HashSet<>(vc1.carrier);
                    combinedCarrier.addAll(vc2.carrier);
                    toAddSemiList.add(new VirtualConnection(connection[1], connection[2], combinedCarrier, vc1.depth + vc2.depth));
                }
            }
        }
        checkRedundancies(toAddSemiList);
        toAdd(toAddSemiList, semiVCs, color, 1);
    }


    private void cleanConnections(){
        blueVCs.removeIf(this::bothBlueMovesOnSameEdge);
        redVCs.removeIf(this::bothRedMovesOnSameEdge);
    }

    public void HProcess() {

        boolean changed = true;
        int ogSize;
        int newSize;
        while (changed) {
            ogSize = blueVCs.size() + blueSemiVCs.size() +  redVCs.size() + redSemiVCs.size();

            applyAndRule(blueSemiVCs, blueVCs, Colors.BLUE.getValue());
            applyAndRule(redSemiVCs, redVCs, Colors.RED.getValue());

            applyOrRule(blueSemiVCs, blueVCs, Colors.BLUE.getValue());
            applyOrRule(redSemiVCs, redVCs, Colors.RED.getValue());

            applyIDEKRule(blueSemiVCs, Colors.BLUE.getValue());
            applyIDEKRule(redSemiVCs, Colors.RED.getValue());

            newSize = blueVCs.size() + blueSemiVCs.size() +  redVCs.size() + redSemiVCs.size();

            changed = ogSize != newSize;
        }
        changed = true;
        while (changed){
            ogSize = blueSemiVCs.size() + redSemiVCs.size();
            applyLastRule(blueSemiVCs, blueVCs, Colors.BLUE.getValue());
            applyLastRule(redSemiVCs, redVCs, Colors.RED.getValue());
            newSize = blueSemiVCs.size() + redSemiVCs.size();
            changed = ogSize != newSize;
        }


        findAllEndVCs();

        if (!blueWinConnections.isEmpty()) bestBlueVC = Collections.min(blueWinConnections);
        if (!blueSemiWinConnections.isEmpty()) bestBlueSemiVC = Collections.min(blueSemiWinConnections);
        if (!redWinConnections.isEmpty()) bestRedVC = Collections.min(redWinConnections);
        if (!redSemiWinConnections.isEmpty()) bestRedSemiVC = Collections.min(redSemiWinConnections);
    }

    private void findEndVCsFromList(HashSet<VirtualConnection> VCs, int type, int color){
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

    private void removeAllWithMove(HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> VCs, Move move){
        Iterator<VirtualConnection> iterator = VCs.iterator();
        VirtualConnection vc;
        while (iterator.hasNext()) {
            vc = iterator.next();
            if (vc.x.equals(move) || vc.y.equals(move) || vc.carrier.contains(move)){
                iterator.remove();
            }
        }

        iterator = semiVCs.iterator();
        while (iterator.hasNext()) {
            vc = iterator.next();
            if (vc.x.equals(move) || vc.y.equals(move) || vc.carrier.contains(move)){
                iterator.remove();
            }
        }
    }

    private void removeAllNextToMove(HashSet<VirtualConnection> semiVCs, Move move){
        Iterator<VirtualConnection> iterator = semiVCs.iterator();
        VirtualConnection vc;
        while (iterator.hasNext()) {
            vc = iterator.next();
            if (vc.y.equals(move)){
                iterator.remove();
            }
        }
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
            removeAllWithMove(redSemiVCs, redVCs, setMove);
            removeAllNextToMove(blueSemiVCs, setMove);
        } else {
            redCells.add(setMove);
            findBaseVCs(setMove, redSemiVCs, redVCs, Colors.RED.getValue());
            removeAllWithMove(blueSemiVCs, blueVCs, setMove);
            removeAllNextToMove(redSemiVCs, setMove);
        }
    }

    private void initializer(){
        blueSemiVCs = new HashSet<VirtualConnection>();
        blueVCs = new HashSet<VirtualConnection>();
        redSemiVCs = new HashSet<VirtualConnection>();
        redVCs = new HashSet<VirtualConnection>();
        blueSemiWinConnections = new HashSet<VirtualConnection>();
        blueWinConnections = new HashSet<VirtualConnection>();
        redSemiWinConnections = new HashSet<VirtualConnection>();
        redWinConnections = new HashSet<VirtualConnection>();
    }

    public Connections(Board board){
        super(board);
        emptyCells = new ArrayList<>(rows*cols);
        blueCells = new ArrayList<>(15);
        redCells = new ArrayList<>(15);
        elecCols = cols + 2;
        elecRows = rows + 2;
        elecBoard = new int[elecCols][elecRows];
        for (int y = 1; y < elecRows - 1; y++){
            elecBoard[0][y] = Colors.BLUE.getValue();
            blueCells.add(new Move(0, y));
            elecBoard[elecCols - 1][y] = Colors.BLUE.getValue();
            blueCells.add(new Move(elecCols - 1, y));
        }
        for (int x = 1; x < elecCols - 1; x++){
            elecBoard[x][0] = Colors.RED.getValue();
            redCells.add(new Move(x, 0));
            elecBoard[x][elecRows - 1] = Colors.RED.getValue();
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
        blueSemiVCs = new HashSet<VirtualConnection>(other.blueSemiVCs);
        blueVCs = new HashSet<VirtualConnection>(other.blueVCs);
        redSemiVCs = new HashSet<VirtualConnection>(other.redSemiVCs);
        redVCs = new HashSet<VirtualConnection>(other.redVCs);

        blueWinConnections = new HashSet<>();
        blueSemiWinConnections = new HashSet<>();
        redWinConnections = new HashSet<>();
        redSemiWinConnections = new HashSet<>();
    }
}