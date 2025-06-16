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

    HashSet<VirtualConnection> blueEdgeConnections;
    HashSet<VirtualConnection> redEdgeConnections;


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

    private void IDEKHelper(HashSet<VirtualConnection> toAddSemiList, HashSet<VirtualConnection> newCheckSemiVCs, HashSet<VirtualConnection> semiVCs) {
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
            for (VirtualConnection checkVC : newCheckSemiVCs) {
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

    private HashSet<VirtualConnection> applyIDEKRule(HashSet<VirtualConnection> newCheckSemiVCs, HashSet<VirtualConnection> semiVCs, int color) { //Måske en regel
        HashSet<VirtualConnection> toAddSemiList = new HashSet<>();
        Move[] connection;
        HashSet<Move> combinedCarrier;

        for (VirtualConnection vc1 : semiVCs) {
            if (vc1.depth != 1 && !vc1.carrier.isEmpty()){
                continue;
            }
            for (VirtualConnection vc2 : newCheckSemiVCs) {
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
        IDEKHelper(toAddSemiList, newCheckSemiVCs, semiVCs);
        return toAdd(toAddSemiList, newCheckSemiVCs, semiVCs, color, 1);
    }

    private int[][] createGraph(HashSet<VirtualConnection> VCs, int n) {
        int[][] adjMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(adjMatrix[i], 100);
        }
        int x;
        int y;

        for (VirtualConnection vc : VCs) {
            x = vc.x.val(elecCols);
            y = vc.y.val(elecCols);

            if (adjMatrix[x][y] > vc.depth) {
                adjMatrix[x][y] = vc.depth;
                adjMatrix[y][x] = vc.depth;
            }
        }
        return adjMatrix;
    }

    private int[][] semiGraph(int[][] baseGraph, VirtualConnection semiVC, int n) {

        int[][] newGraph = new int[n][n];

        for (int i = 0; i < n; i++) {
            System.arraycopy(baseGraph[i], 0, newGraph[i], 0, n);
        }

        int x = semiVC.x.val(elecCols);
        int y = semiVC.y.val(elecCols);

        if (newGraph[x][y] > semiVC.depth) {
            newGraph[x][y] = semiVC.depth;
            newGraph[y][x] = semiVC.depth;
        }
        return newGraph;
    }

    private int tryAllSemis(HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> VCs){
        int source = 7;
        int sink = 13;

        int bestDist = 1000;
        int currDist;
        int[][] currGraph;
        int n = elecCols * elecRows;
        int[][] baseGraph = createGraph(VCs, n);

        for (VirtualConnection semiVC : semiVCs){
            currGraph = semiGraph(baseGraph, semiVC, n);
            currDist = dijkstra(currGraph, source)[sink];
            if (bestDist > currDist){
                bestDist = currDist;
            }
        }
        return bestDist;
    }

    public static int[] dijkstra(int[][] graph, int source) {
        int n = graph.length;
        int[] dist = new int[n];          // Shortest distances from source
        boolean[] visited = new boolean[n]; // Track visited vertices

        // Initialize distances to infinity and visited to false
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        for (int i = 0; i < n - 1; i++) {
            int u = minDistance(dist, visited);
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visited[v] &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }
        return dist;
    }

    private static int minDistance(int[] dist, boolean[] visited) {
        int min = Integer.MAX_VALUE, minIndex = -1;
        for (int v = 0; v < dist.length; v++) {
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    private void cleanConnections(){
        Iterator<VirtualConnection> iterator = blueVCs.iterator();
        VirtualConnection vc;
        while (iterator.hasNext()){
            vc = iterator.next();
            if (bothBlueMovesOnSameEdge(vc)){
                blueEdgeConnections.add(vc);
                iterator.remove();
            }
        }
        iterator = redVCs.iterator();
        while (iterator.hasNext()){
            vc = iterator.next();
            if (bothRedMovesOnSameEdge(vc)){
                redEdgeConnections.add(vc);
                iterator.remove();
            }
        }
    }

    public int[] HProcess() {
        int[] retArr = new int[2];
        boolean changed = true;
        int ogSize;
        int newSize;
        HashSet<VirtualConnection> newBlueVCs = new HashSet<>();
        HashSet<VirtualConnection> newBlueSemiVCs = new HashSet<>();
        HashSet<VirtualConnection> newRedVCs = new HashSet<>();
        HashSet<VirtualConnection> newRedSemiVCs = new HashSet<>();

        HashSet<VirtualConnection> checkNewBlueVCs = new HashSet<>(blueVCs);
        HashSet<VirtualConnection> checkNewBlueSemiVCs = new HashSet<>(blueSemiVCs);
        HashSet<VirtualConnection> checkNewRedVCs = new HashSet<>(redVCs);
        HashSet<VirtualConnection> checkNewRedSemiVCs = new HashSet<>(redSemiVCs);

        blueVCs.clear();
        blueSemiVCs.clear();
        redVCs.clear();
        redSemiVCs.clear();


        // And returner HashSet arr, 0 = VC, 1 = semi. Or returner VC, IDEK return Semi
        HashSet<VirtualConnection>[] andBlueNewHelper = new HashSet[2];
        HashSet<VirtualConnection>[] andRedNewHelper = new HashSet[2];

        newBlueSemiVCs.addAll(applyIDEKRule(checkNewBlueSemiVCs, checkNewBlueSemiVCs, Colors.BLUE.getValue()));
        newRedSemiVCs.addAll(applyIDEKRule(checkNewRedSemiVCs, checkNewRedSemiVCs, Colors.RED.getValue()));

        while (changed) {

            ogSize = blueVCs.size() + redVCs.size() +  blueSemiVCs.size() + redSemiVCs.size();

            andBlueNewHelper = applyAndRule(checkNewBlueSemiVCs, blueSemiVCs, checkNewBlueVCs, blueVCs, Colors.BLUE.getValue());
            andRedNewHelper = applyAndRule(checkNewRedSemiVCs, redSemiVCs, checkNewRedVCs, redVCs, Colors.RED.getValue());

            newBlueVCs.addAll(applyOrRule(checkNewBlueSemiVCs, blueSemiVCs));
            newRedVCs.addAll(applyOrRule(checkNewRedSemiVCs, redSemiVCs));

            newBlueSemiVCs.addAll(applyIDEKRule(checkNewBlueSemiVCs, blueSemiVCs, Colors.BLUE.getValue()));
            newRedSemiVCs.addAll(applyIDEKRule(checkNewRedSemiVCs, redSemiVCs, Colors.RED.getValue()));

            newBlueVCs.addAll(andBlueNewHelper[0]);
            newRedVCs.addAll(andRedNewHelper[0]);
            newBlueSemiVCs.addAll(andBlueNewHelper[1]);
            newRedSemiVCs.addAll(andRedNewHelper[1]);

            checkRedundancies(newBlueVCs);
            checkRedundancies(newRedVCs);
            checkRedundancies(newBlueSemiVCs);
            checkRedundancies(newRedSemiVCs);

            blueVCs.addAll(checkNewBlueVCs);
            blueSemiVCs.addAll(checkNewBlueSemiVCs);
            redVCs.addAll(checkNewRedVCs);
            redSemiVCs.addAll(checkNewRedSemiVCs);

            checkNewBlueVCs.clear();
            checkNewBlueSemiVCs.clear();
            checkNewRedVCs.clear();
            checkNewRedSemiVCs.clear();

            checkNewBlueVCs.addAll(newBlueVCs);
            checkNewBlueSemiVCs.addAll(newBlueSemiVCs);
            checkNewRedVCs.addAll(newRedVCs);
            checkNewRedSemiVCs.addAll(newRedSemiVCs);

            newBlueVCs.clear();
            newBlueSemiVCs.clear();
            newRedVCs.clear();
            newRedSemiVCs.clear();

            newSize = blueVCs.size() + redVCs.size() +  blueSemiVCs.size() + redSemiVCs.size();
            changed = newSize != ogSize;
        }

        findAllEndVCs();

        if (!blueWinConnections.isEmpty()) bestBlueVC = Collections.min(blueWinConnections);
        if (!blueSemiWinConnections.isEmpty()) bestBlueSemiVC = Collections.min(blueSemiWinConnections);
        if (!redWinConnections.isEmpty()) bestRedVC = Collections.min(redWinConnections);
        if (!redSemiWinConnections.isEmpty()) bestRedSemiVC = Collections.min(redSemiWinConnections);

        blueVCs.addAll(blueEdgeConnections);
        retArr[0] = tryAllSemis(blueSemiVCs, blueVCs);
        redVCs.addAll(redEdgeConnections);
        retArr[1] = tryAllSemis(redSemiVCs, redVCs);

        return retArr;
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
        blueEdgeConnections = new HashSet<>();
        redEdgeConnections = new HashSet<>();

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

        blueEdgeConnections = new HashSet<VirtualConnection>(other.blueEdgeConnections);
        redEdgeConnections = new HashSet<VirtualConnection>(other.redEdgeConnections);
    }
}