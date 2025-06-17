package com.hex.algorithms.minimax;
import com.hex.algorithms.minimax.connectionsHelpers.SetHolder;
import com.hex.algorithms.minimax.connectionsHelpers.SimpleConnectionsLogic;
import com.hex.components.Board;
import java.util.*;

public class Connections extends SimpleConnectionsLogic {

    HashSet<VirtualConnection> blueEdgeConnections;
    HashSet<VirtualConnection> redEdgeConnections;

    private int[][] createGraph(HashSet<VirtualConnection> VCs, int n) {
        int[][] adjMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(adjMatrix[i], 1000);
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

    private int getShortestEnd(HashSet<VirtualConnection> VCs, int source, int target){
        int n = elecCols * elecRows;
        return dijkstra(createGraph(VCs, n), source)[target];
    }

    private int tryAllSemis(HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> VCs, int source, int target){
        int bestDist = 10000;
        int currDist;
        int n = elecCols * elecRows;
        int[][] baseGraph = createGraph(VCs, n);
        int oldVal;

        for (VirtualConnection semiVC : semiVCs){
            int x = semiVC.x.val(elecCols);
            int y = semiVC.y.val(elecCols);
            oldVal = baseGraph[x][y];
            if (oldVal > semiVC.depth) {
                baseGraph[x][y] = semiVC.depth;
                baseGraph[y][x] = semiVC.depth;
            }
            currDist = dijkstra(baseGraph, source)[target];
            if (bestDist > currDist){
                bestDist = currDist;
            }
            baseGraph[x][y] = oldVal;
            baseGraph[y][x] = oldVal;
        }
        return bestDist;
    }

    public static int[] dijkstra(int[][] graph, int source) {
        int n = graph.length;
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        // min‑heap of (distance, vertex)
        PriorityQueue<Vertex> pq = new PriorityQueue<>();
        pq.add(new Vertex(source, 0));

        while (!pq.isEmpty()) {
            Vertex u = pq.poll();
            if (visited[u.id]) continue;
            visited[u.id] = true;

            // relax neighbors
            for (int v = 0; v < n; v++) {
                int weight = graph[u.id][v];
                if (!visited[v] && u.dist + weight < dist[v]) {
                    dist[v] = u.dist + weight;
                    pq.add(new Vertex(v, dist[v]));
                }
            }
        }

        return dist;
    }

    private static class Vertex implements Comparable<Vertex> {
        final int id;
        final int dist;
        Vertex(int id, int dist) {
            this.id = id;
            this.dist = dist;
        }
        @Override
        public int compareTo(Vertex other) {
            return Integer.compare(this.dist, other.dist);
        }
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

    private void AFAIKRuleHelper(HashSet<VirtualConnection> toAdd, HashSet<VirtualConnection> checkNewVCs, HashSet<VirtualConnection> VCs){
        Iterator<VirtualConnection> iterator = toAdd.iterator();
        VirtualConnection toAddVC;
        boolean removed;
        while (iterator.hasNext()){
            toAddVC = iterator.next();
            removed = false;
            for (VirtualConnection checkVC : VCs){
                if (toAddVC.isSubset(checkVC)){
                    iterator.remove();
                    removed = true;
                    break;
                }
            }
            if (removed) { continue; }
            for (VirtualConnection checkVC : checkNewVCs){
                if (toAddVC.isSubset(checkVC)){
                    iterator.remove();
                }
            }
        }
    }

    private void AFAIKRuleHelperSecond(HashSet<VirtualConnection> toAddSemiList, HashSet<VirtualConnection> newCheckSemiVCs, HashSet<VirtualConnection> semiVCs) {
        List<VirtualConnection> toRemove = new ArrayList<>();

        for (VirtualConnection toAdd : toAddSemiList) {
            if (toRemove.contains(toAdd)) { continue; }
            for (VirtualConnection checkVC : toAddSemiList) {
                if (toAdd == checkVC) {
                    continue;
                }
                if ((toAdd.depth == checkVC.depth &&
                        toAdd.carrier.size() == checkVC.carrier.size() &&
                        toAdd.x.equals(checkVC.y) && toAdd.y.equals(checkVC.x)) ||
                        (toAdd.depth <= checkVC.depth && toAdd.equalEnds(checkVC) && (toAdd.carrier.contains(checkVC.criticalCell) || checkVC.carrier.contains(toAdd.criticalCell))))
                {
                    toRemove.add(checkVC);
                }
            }
            for (VirtualConnection checkVC : semiVCs) {
                if ((toAdd.depth == checkVC.depth &&
                        toAdd.carrier.size() == checkVC.carrier.size() &&
                        toAdd.x.equals(checkVC.y) && toAdd.y.equals(checkVC.x)) ||
                        (checkVC.depth <= toAdd.depth && toAdd.equalEnds(checkVC) && (toAdd.carrier.contains(checkVC.criticalCell) || checkVC.carrier.contains(toAdd.criticalCell)))) {
                    toRemove.add(toAdd);
                    break;
                }
            }
            for (VirtualConnection checkVC : newCheckSemiVCs) {
                if ((toAdd.depth == checkVC.depth &&
                        toAdd.carrier.size() == checkVC.carrier.size() &&
                        toAdd.x.equals(checkVC.y) && toAdd.y.equals(checkVC.x)) ||
                        (checkVC.depth <= toAdd.depth && toAdd.equalEnds(checkVC) && (toAdd.carrier.contains(checkVC.criticalCell) || checkVC.carrier.contains(toAdd.criticalCell)))) {
                    toRemove.add(toAdd);
                    break;
                }
            }
        }
        toRemove.forEach(toAddSemiList::remove);
    }

    public HashSet<VirtualConnection> applyAFAIKRule(HashSet<VirtualConnection> checkNewSemiVCs, HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> checkNewVCs, HashSet<VirtualConnection> VCs, int color) {
        HashSet<VirtualConnection> toAddSemiList = new HashSet<>();

        for (VirtualConnection vc1 : checkNewSemiVCs) {
            for (VirtualConnection vc2 : checkNewSemiVCs) {
                if (vc1 == vc2 || !vc1.sameCriticalCell(vc2)) { continue; }
                Move[] connection = vc1.getConnectingEnd(vc2);
                if (connection != null && (!vc1.endIsCarrier(vc2) && !vc2.endIsCarrier(vc1)) ) {
                    HashSet<Move> combinedCarrier = new HashSet<>(vc1.carrier);
                    combinedCarrier.addAll(vc2.carrier);
                    VirtualConnection currVC = new VirtualConnection(connection[1], connection[2], combinedCarrier, vc1.depth + vc2.depth-1, vc1.criticalCell);
                    combinedCarrier.add(connection[0]);
                    toAddSemiList.add(currVC);
                }
            }
            for (VirtualConnection vc2 : semiVCs) {
                if (!vc1.sameCriticalCell(vc2)) { continue; }
                Move[] connection = vc1.getConnectingEnd(vc2);
                if (connection != null && (!vc1.endIsCarrier(vc2) && !vc2.endIsCarrier(vc1)) ) {
                    HashSet<Move> combinedCarrier = new HashSet<>(vc1.carrier);
                    combinedCarrier.addAll(vc2.carrier);
                    VirtualConnection currVC = new VirtualConnection(connection[1], connection[2], combinedCarrier, vc1.depth + vc2.depth-1, vc1.criticalCell);
                    combinedCarrier.add(connection[0]);
                    toAddSemiList.add(currVC);
                }
            }
        }
        if (color == Colors.RED.getValue()) toAddSemiList.removeIf(this::bothRedMovesOnSameEdge);
        else if (color == Colors.BLUE.getValue()) toAddSemiList.removeIf(this::bothBlueMovesOnSameEdge);
        checkRedundancies(toAddSemiList);
        AFAIKRuleHelper(toAddSemiList, checkNewVCs, VCs);
        AFAIKRuleHelperSecond(toAddSemiList, checkNewSemiVCs, semiVCs);
        return toAdd(toAddSemiList, checkNewSemiVCs, semiVCs, color, 1);
    }

    private void endRuleBlue(HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> VCs){
        HashMap<Move, HashSet<VirtualConnection>> moveLeftMap = new HashMap<>();
        HashMap<Move, HashSet<VirtualConnection>> moveRightMap = new HashMap<>();
        for (VirtualConnection vc : semiVCs){
            if (moveOnLeftBlueEdge(vc.x)){
                if (!moveLeftMap.containsKey(vc.y)){
                    HashSet<VirtualConnection> set = new HashSet<>();
                    set.add(vc);
                    moveLeftMap.put(vc.y, set);
                } else {
                    moveLeftMap.get(vc.y).add(vc);
                }
            }
            else if (moveOnLeftBlueEdge(vc.y)){
                if (!moveLeftMap.containsKey(vc.x)){
                    HashSet<VirtualConnection> set = new HashSet<>();
                    set.add(vc);
                    moveLeftMap.put(vc.x, set);
                } else {
                    moveLeftMap.get(vc.x).add(vc);
                }
            }
            if (moveOnRightBlueEdge(vc.x)){
                if (!moveRightMap.containsKey(vc.y)){
                    HashSet<VirtualConnection> set = new HashSet<>();
                    set.add(vc);
                    moveRightMap.put(vc.y, set);
                } else {
                    moveRightMap.get(vc.y).add(vc);
                }
            }
            else if (moveOnRightBlueEdge(vc.y)){
                if (!moveRightMap.containsKey(vc.x)){
                    HashSet<VirtualConnection> set = new HashSet<>();
                    set.add(vc);
                    moveRightMap.put(vc.x, set);
                } else {
                    moveRightMap.get(vc.x).add(vc);
                }
            }
        }
        HashSet<VirtualConnection> currSet;
        HashSet<Move> currCarrier = new HashSet<>();
        int maxDepth = 0;
        for (Move move : moveRightMap.keySet()){
            currSet = moveRightMap.get(move);
            checkRedundancies(currSet);
            if (currSet.size() > 1){
                currCarrier.clear();
                maxDepth = 0;
                for (VirtualConnection vc : currSet){
                    currCarrier.addAll(vc.carrier);
                    maxDepth = Math.max(maxDepth, vc.depth);
                }
                VCs.add(new VirtualConnection(move, new Move(elecCols-1, 1), currCarrier, maxDepth));
            }
        }
        for (Move move : moveLeftMap.keySet()){
            currSet = moveLeftMap.get(move);
            checkRedundancies(currSet);
            if (currSet.size() > 1){
                currCarrier.clear();
                maxDepth = 0;
                for (VirtualConnection vc : currSet){
                    currCarrier.addAll(vc.carrier);
                    maxDepth = Math.max(maxDepth, vc.depth);
                }
                VCs.add(new VirtualConnection(move, new Move(0, 1), currCarrier, maxDepth));
            }
        }
    }

    private void endRuleRed(HashSet<VirtualConnection> semiVCs, HashSet<VirtualConnection> VCs){
        HashMap<Move, HashSet<VirtualConnection>> moveLeftMap = new HashMap<>();
        HashMap<Move, HashSet<VirtualConnection>> moveRightMap = new HashMap<>();
        for (VirtualConnection vc : semiVCs){
            if (moveOnLeftRedEdge(vc.x)){
                if (!moveLeftMap.containsKey(vc.y)){
                    HashSet<VirtualConnection> set = new HashSet<>();
                    set.add(vc);
                    moveLeftMap.put(vc.y, set);
                } else {
                    moveLeftMap.get(vc.y).add(vc);
                }
            }
            else if (moveOnLeftRedEdge(vc.y)){
                if (!moveLeftMap.containsKey(vc.x)){
                    HashSet<VirtualConnection> set = new HashSet<>();
                    set.add(vc);
                    moveLeftMap.put(vc.x, set);
                } else {
                    moveLeftMap.get(vc.x).add(vc);
                }
            }
            if (moveOnRightRedEdge(vc.x)){
                if (!moveRightMap.containsKey(vc.y)){
                    HashSet<VirtualConnection> set = new HashSet<>();
                    set.add(vc);
                    moveRightMap.put(vc.y, set);
                } else {
                    moveRightMap.get(vc.y).add(vc);
                }
            }
            else if (moveOnRightRedEdge(vc.y)){
                if (!moveRightMap.containsKey(vc.x)){
                    HashSet<VirtualConnection> set = new HashSet<>();
                    set.add(vc);
                    moveRightMap.put(vc.x, set);
                } else {
                    moveRightMap.get(vc.x).add(vc);
                }
            }
        }
        HashSet<VirtualConnection> currSet;
        HashSet<Move> currCarrier = new HashSet<>();
        int maxDepth = 0;
        for (Move move : moveRightMap.keySet()){
            currSet = moveRightMap.get(move);
            checkRedundancies(currSet);
            if (currSet.size() > 1){
                currCarrier.clear();
                maxDepth = 0;
                for (VirtualConnection vc : currSet){
                    currCarrier.addAll(vc.carrier);
                    maxDepth = Math.max(maxDepth, vc.depth);
                }
                VCs.add(new VirtualConnection(move, new Move(1, elecRows-1), currCarrier, maxDepth));
            }
        }
        for (Move move : moveLeftMap.keySet()){
            currSet = moveLeftMap.get(move);
            checkRedundancies(currSet);
            if (currSet.size() > 1){
                currCarrier.clear();
                maxDepth = 0;
                for (VirtualConnection vc : currSet){
                    currCarrier.addAll(vc.carrier);
                    maxDepth = Math.max(maxDepth, vc.depth);
                }
                VCs.add(new VirtualConnection(move, new Move(1, 0), currCarrier, maxDepth));
            }
        }
    }

    public int[] HProcess(SetHolder setHolder) {
        int[] retArr = new int[4];
        boolean changed = true;
        int ogSize;
        int newSize;

        cleanConnections();

        setHolder.clearAll();

        HashSet<VirtualConnection> newBlueVCs = setHolder.newBlueVCs;
        HashSet<VirtualConnection> newBlueSemiVCs = setHolder.newBlueSemiVCs;
        HashSet<VirtualConnection> newRedVCs = setHolder.newRedVCs;
        HashSet<VirtualConnection> newRedSemiVCs = setHolder.newRedSemiVCs;
        HashSet<VirtualConnection> checkNewBlueVCs = setHolder.checkNewBlueVCs;
        HashSet<VirtualConnection> checkNewBlueSemiVCs = setHolder.checkNewBlueSemiVCs;
        HashSet<VirtualConnection> checkNewRedVCs = setHolder.checkNewRedVCs;
        HashSet<VirtualConnection> checkNewRedSemiVCs = setHolder.checkNewRedSemiVCs;

        checkNewBlueVCs.addAll(blueVCs);
        checkNewBlueSemiVCs.addAll(blueSemiVCs);
        checkNewRedVCs.addAll(redVCs);
        checkNewRedSemiVCs.addAll(redSemiVCs);

        blueVCs.clear();
        blueSemiVCs.clear();
        redVCs.clear();
        redSemiVCs.clear();

        // And returner HashSet arr, 0 = VC, 1 = semi. Or returner VC, IDEK return Semi
        HashSet<VirtualConnection>[] andBlueNewHelper = new HashSet[2];
        HashSet<VirtualConnection>[] andRedNewHelper = new HashSet[2];

        for (int i = 0; i < 3; i++) {

            ogSize = blueVCs.size() + redVCs.size() +  blueSemiVCs.size() + redSemiVCs.size();

            andBlueNewHelper = applyAndRule(checkNewBlueSemiVCs, blueSemiVCs, checkNewBlueVCs, blueVCs, Colors.BLUE.getValue());
            andRedNewHelper = applyAndRule(checkNewRedSemiVCs, redSemiVCs, checkNewRedVCs, redVCs, Colors.RED.getValue());

            newBlueVCs.addAll(applyOrRule(checkNewBlueSemiVCs, blueSemiVCs));
            newRedVCs.addAll(applyOrRule(checkNewRedSemiVCs, redSemiVCs));

            newBlueSemiVCs.addAll(applyAFAIKRule(checkNewBlueSemiVCs, blueSemiVCs, checkNewBlueVCs, blueVCs, Colors.BLUE.getValue()));
            newRedSemiVCs.addAll(applyAFAIKRule(checkNewRedSemiVCs, redSemiVCs, checkNewRedVCs, redVCs, Colors.RED.getValue()));


            newBlueVCs.addAll(andBlueNewHelper[0]);
            newRedVCs.addAll(andRedNewHelper[0]);
            newBlueSemiVCs.addAll(andBlueNewHelper[1]);
            newRedSemiVCs.addAll(andRedNewHelper[1]);

            checkRedundancies(newBlueVCs);
            checkRedundancies(newRedVCs);
            checkRedundancies(newBlueSemiVCs);
            checkRedundancies(newRedSemiVCs);

            newBlueVCs.removeIf(vc -> blueVCs.contains(vc) || checkNewBlueVCs.contains(vc));
            newBlueSemiVCs.removeIf(vc -> blueSemiVCs.contains(vc) || checkNewBlueSemiVCs.contains(vc));
            newRedVCs.removeIf(vc -> redVCs.contains(vc) || checkNewRedVCs.contains(vc));
            newRedSemiVCs.removeIf(vc -> redSemiVCs.contains(vc) || checkNewRedSemiVCs.contains(vc));

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
            if (!changed) { break; }
        }

        endRuleBlue(blueSemiVCs, blueVCs);
        endRuleRed(redSemiVCs, redVCs);

        blueVCs.addAll(blueEdgeConnections);
        redVCs.addAll(redEdgeConnections);
        retArr[0] = getShortestEnd(blueVCs, elecRows, 2*elecCols-1);
        retArr[1] = tryAllSemis(blueSemiVCs, blueVCs, elecRows, 2*elecCols-1);

        retArr[2] = getShortestEnd(redVCs, 1, (elecRows-1)*elecCols+1);
        retArr[3] = tryAllSemis(redSemiVCs, redVCs, 1, (elecRows-1)*elecCols+1);

        return retArr;
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

        blueEdgeConnections = new HashSet<VirtualConnection>(other.blueEdgeConnections);
        redEdgeConnections = new HashSet<VirtualConnection>(other.redEdgeConnections);
    }
}