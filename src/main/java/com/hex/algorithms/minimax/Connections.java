package com.hex.algorithms.minimax;
import com.hex.algorithms.minimax.connectionsHelpers.SimpleConnectionsLogic;
import com.hex.components.Board;
import java.util.*;

public class Connections extends SimpleConnectionsLogic {

    ArrayList<VirtualConnection> blueSemiWinConnections;
    ArrayList<VirtualConnection> blueWinConnections;
    ArrayList<VirtualConnection> redSemiWinConnections;
    ArrayList<VirtualConnection> redWinConnections;
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

    public boolean applyLastRule(ArrayList<VirtualConnection> semiVCs, ArrayList<VirtualConnection> VCs, int color) { //Måske en regel
        ArrayList<VirtualConnection> toAddSemiList = new ArrayList<>();
        Move[] connection;
        HashSet<Move> combinedCarrier;
        boolean isLeft = false;

        for (VirtualConnection vc1 : semiVCs) {
            if (color == Colors.BLUE.getValue() && movesOnLeftBlueEdge(vc1)) {
                isLeft = true;
            }
            if (color == Colors.BLUE.getValue() && !movesOnRightBlueEdge(vc1)) {
                continue;
            }
            if (color == Colors.RED.getValue() && movesOnLeftRedEdge(vc1)) {
                isLeft = true;
            }
            else if (color == Colors.RED.getValue() && !movesOnRightRedEdge(vc1)) {
                continue;
            }

            for (VirtualConnection vc2 : VCs) {

                if (isLeft){
                    if (color == Colors.BLUE.getValue() && !movesOnRightBlueEdge(vc2)) {
                        continue;
                    }
                    if (color == Colors.RED.getValue() && !movesOnRightRedEdge(vc2)) {
                        continue;
                    }
                }
                else {
                    if (color == Colors.BLUE.getValue() && !movesOnLeftBlueEdge(vc2)) {
                        continue;
                    }
                    if (color == Colors.RED.getValue() && !movesOnLeftRedEdge(vc2)) {
                        continue;
                    }
                }

                connection = vc1.getConnectingEnd(vc2);
                if (connection != null) {
                    combinedCarrier = new HashSet<>(vc1.carrier);
                    combinedCarrier.addAll(vc2.carrier);
                    toAddSemiList.add(new VirtualConnection(connection[1], connection[2], combinedCarrier, vc1.depth + vc2.depth - 1));
                }
            }
        }
        checkRedundancies(toAddSemiList);
        boolean a = toAdd(toAddSemiList, semiVCs, color, 1);
        return a;
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
            bestBlueVC = Collections.min(blueWinConnections);
        } if (!blueSemiWinConnections.isEmpty()){
            bestBlueSemiVC = Collections.min(blueSemiWinConnections);
        } if (!redWinConnections.isEmpty()){
            bestRedVC = Collections.min(redWinConnections);
        } if (!redSemiWinConnections.isEmpty()){
            bestRedSemiVC = Collections.min(redSemiWinConnections);
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