package GomokuAIproject.Greg3;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeoutException;

import GomokuAIproject.Board;
import GomokuAIproject.Engine;
import GomokuAIproject.EngineHelpers.SuperMove;

public class Greg3 implements Engine{

    private G3VirtualBoard myVirtualBoard;
    private boolean isOpponentBlack;
    private int depth;
    private TranspositionTable table;
    private KillerMoveTable killerMoves;
    private double timeToPlay;
    private double OGtimeToPlay;
    private double totalTimeLeft;
    private int totalDepth;
    private double EFBtotal;
    private double totalGenerations;

    // stats
    private long startTime;
    private int TThits;
    private int nodes;
    private int prunes;
    private double movesPlayed;
    private int leafNodes;

    private int generation;
    private boolean fix;

    private boolean searchThisMoveFirst;
    private SuperMove bestMoveFound;
    private ArrayList<Integer> candidateBestMoves;

    private static final int SAFETY_MARGIN = 50;
    private static final int UNCERTAINTY_PENALTY = 50;
    private static final int QUIESCENCE_DEPTH_LIMIT = 4;
    private static final int DEPTH_LIMIT = 20;   //15

    // History Heuristic implementation
    private static int[] historyTable;
    private static final Comparator<Integer> histoyCompare = 
        (i1, i2) -> Integer.compare(historyTable[i2], historyTable[i1]);

    private static double[] dependentTable;
    private static final Comparator<Integer> dependentCompare = 
        (i1, i2) -> Double.compare(dependentTable[i2], dependentTable[i1]);

    
    public Greg3(boolean isOpponentBlack, double timeToPlay, boolean testSegments, boolean fix){
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard = new G3VirtualBoard(isOpponentBlack, testSegments, fix);
        table = new TranspositionTable(G3Constants.TTSize);
        killerMoves = new KillerMoveTable(DEPTH_LIMIT, 3);
        historyTable = new int[169];
        dependentTable = new double[169];
        Arrays.fill(dependentTable, 1);
        this.timeToPlay = timeToPlay;
        OGtimeToPlay = timeToPlay;
        generation = 0;
        nodes = 0;
        totalDepth = 0;
        movesPlayed = 0;
        this.fix = fix;
        totalTimeLeft = 295;
        EFBtotal = 0;
        totalGenerations = 0;
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        // lastEvaluation = 0;
        // currentEvaluation = 0;
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard.setIsOpponentBlack(isOpponentBlack);
        table.clear();
        killerMoves.clear();
        historyTable = new int[169];
        generation = 0;
        //System.out.println("Fix? " + fix + ", Average Depth: " + totalDepth / movesPlayed);
        //System.out.println("Average Nodes Per Second: " + (int)((nodes / movesPlayed) / timeToPlay) + " N/s");
    }

    public boolean getIsOpponentBlack(){
        return isOpponentBlack;
    }

    public int playFromPosition(Board board){

        generation++;
        totalGenerations++;
        movesPlayed++;

        myVirtualBoard.sync();  // syncs to current board state and clears move history
        myVirtualBoard.getEvaluation();
        for(int i = 0; i < historyTable.length; i++){
            historyTable[i] = Math.clamp((int)Math.log(historyTable[i] + 1), 0, 10);
        }

        leafNodes = 0;
        TThits = 0;
        nodes = 0;
        prunes = 0;

        ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
        bestMoveFound = new SuperMove(moves.get(0), 0);
        candidateBestMoves = new ArrayList<Integer>();
        searchThisMoveFirst = false;

        int finalDepth = iterativeSearch(DEPTH_LIMIT, board);

        int myMove;
        myMove = bestMoveFound.getMoveLocation();
        System.out.println("--------------");
        System.out.println("Move Played: " + bestMoveFound.getMoveLocation());
        System.out.println("Depth reached: " + (finalDepth));
        //System.out.println("Evaluation: " + bestMoveFound.getScore());
        System.out.println("Positions searched: " + ((int)(nodes / 10000)/100.0) + "M");
        int evaluation = (int)bestMoveFound.getScore();
        int mySign = (isOpponentBlack)? 1: -1;
        evaluation *= mySign;
        if(evaluation <= -90000){
            System.out.println("I'm dead");
        }else if(evaluation <= -220){
            System.out.println("Strongly unfavorable");
        }else if(evaluation < -120){
            System.out.println("Unfavorable");
        }else if(evaluation <= 120){
            System.out.println("Neutral");
        }else if(evaluation < 220){
            System.out.println("Favorable");
        }else if(evaluation < 90000){
            System.out.println("Strongly favorable");
        }else{
            System.out.println("You're dead");
        }
        //System.out.println("Fix? " + fix + " b: " + ((int)(Math.pow(leafNodes, 0.2)*100.0))/100.0);

        return myMove;

    }

    // plays in a human-like manner
    boolean foundWin;
    boolean previouslyFoundWin;
    double timeTakenThisTurn = 0;
    boolean playedForcingMove = false;
    public int playFromPositionHuman(Board board){
        // if(!play){
        //     return -1;
        // }
        if(generation == 0){
            foundWin = false;
            previouslyFoundWin = false;
        }
        foundWin = false;
        generation++;
        movesPlayed++;
        myVirtualBoard.sync();  // syncs to current board state and clears move history
        int evaluation = myVirtualBoard.getEvaluation();
        if(bestMoveFound != null){
            evaluation = (int)bestMoveFound.getScore();
        }
        //table.clear();
        TThits = 0;
        nodes = 0;
        prunes = 0;
        ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
        LocationList[] threatMapList = myVirtualBoard.fetchThreatMapList();
        bestMoveFound = new SuperMove(moves.get(0), 0);
        SuperMove lastBestMove = new SuperMove(-1, -1);
        searchThisMoveFirst = false;
        // bring old killer moves one ply closer to root for new iteration
        killerMoves.shiftBackward();
        startTime = System.nanoTime();

        // determine how much time to spend
        int offset = (isOpponentBlack)? 0: 4;
        int offset2 = (isOpponentBlack)? 4: 0;
        boolean beingForced = threatMapList[G3Constants.FIVE_THREAT_INDEX + offset].getSize() > 0;
        if(beingForced){
            timeToPlay = 0.5;
        }else{
            timeToPlay = (moves.size() / 40.0) * OGtimeToPlay * (1 + Math.random() * 0.4 - 0.2);
            if(myVirtualBoard.getMoveHistory().size() < 4){
                timeToPlay *= 0.1;
            }
        }
        int mySign = (isOpponentBlack)? 1: -1;
        if(Math.abs(evaluation) < 100){
            System.out.println("neutral");
            timeToPlay *= 1;
        }else if(evaluation * mySign >= 150){
            System.out.println("winning");
            timeToPlay *= 0.8;
        }else if(evaluation * mySign <= -110){
            System.out.println("losing");
            timeToPlay *= 1.3;
        }
        totalTimeLeft -= timeTakenThisTurn;
        double timeTaken = 300 - totalTimeLeft;
        double timeManagementConstant = (1/2.9)*Math.log(-(timeTaken/100.0)+3.1)+0.6;
        timeManagementConstant = Math.max(timeManagementConstant, 0.05);
        System.out.println("Time Management Multiplier: " + timeManagementConstant);
        timeToPlay *= timeManagementConstant;
        timeToPlay = Math.min(timeToPlay, 18);

        int finalDepth = 1;
        for (int depth = 1; depth <= DEPTH_LIMIT; depth++) {
            try {
                bestMoveFound = minimax(depth, depth, isOpponentBlack, board.getLastMove(),
                    generation, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                searchThisMoveFirst = true;
            } catch (TimeoutException e) {
                //System.out.println("Fix?: " + fix + ", Total Depth " + totalDepth);
                break; // fallback to last bestMove
            }
            finalDepth = depth;
            if(bestMoveFound.getScore() * mySign >= 90000){
                foundWin = true;
                if(!previouslyFoundWin){
                    try{
                        double timeLeftThisTurn = (18 - (System.nanoTime() - startTime) / 1000000000.0)*1000;
                        double timeToWait = Math.min(
                            Math.min(3000 + 2000 * finalDepth, Math.max((long)timeLeftThisTurn, 0)),
                            totalTimeLeft * 300);
                        if(bestMoveFound.getScore() * mySign > 0 && !beingForced){
                            Thread.sleep((long)timeToWait);
                        }
                    }catch(Exception e){}
                }
                break;
            }
            if(lastBestMove.getMoveLocation() == bestMoveFound.getMoveLocation()){
                timeToPlay *= 0.92;
            }
            lastBestMove.set(bestMoveFound.getMoveLocation(), -1);
        }
        previouslyFoundWin = foundWin;
        System.out.println("--------------");
        System.out.println("Move Played: " + bestMoveFound.getMoveLocation());
        System.out.println("Evaluation: " + bestMoveFound.getScore());
        System.out.println("Positions searched: " + nodes);
        System.out.println("Depth reached: " + (finalDepth));
        totalDepth += finalDepth;
        int myMove = bestMoveFound.getMoveLocation();
        // System.out.println("Nodes: " + nodes);
        // System.out.println("TTHits: " + TThits);
        //System.out.println(myMove);
        timeTakenThisTurn = ((System.nanoTime() - startTime)/1000000000.0) + 2;
        return myMove;
    }

    // changes the global variable bestMoveFound
    // returns final depth reached
    public int iterativeSearch(int depthLimit, Board board){
        startTime = System.nanoTime();  // set global variable startTime to track time usage
        double EFB = 1;
        int lastNodes = 1;
        int currentNodes = 1;
        int finalDepth = 0;
        for (int depth = 1; depth <= depthLimit; depth++) {
            try{
                bestMoveFound = minimax(depth, depth, isOpponentBlack, board.getLastMove(),
                    generation, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                searchThisMoveFirst = true;
            }catch (TimeoutException e) {
                break;
            }
            finalDepth = depth;
            lastNodes = currentNodes;
            currentNodes = nodes;
            //System.out.println("C: " + currentNodes + "/ L: " + lastNodes);
            EFB *= (currentNodes * 1.0 / lastNodes);
            //System.out.println(EFB);
        }
        EFB = Math.pow(EFB, 1.0/finalDepth);
        EFBtotal += EFB;
        System.out.println("Fix? " + fix + " EFB avg: " + EFBtotal / totalGenerations);
        return finalDepth;
    }

    // also adds reach moves (gap of 1 if also a threat of some kind)
    private void orderMoves(ArrayList<Integer> moves, LocationList[] threatMapList, long hash, int plyFromRoot,
        boolean forBlack){

        Arrays.fill(dependentTable, 0);

        // find maxHistoryValue so it can be normalized
        int maxHistoryValue = Integer.MIN_VALUE;
        for(int historyValue: historyTable){
            if(historyValue > maxHistoryValue){
                maxHistoryValue = historyValue;
            }
        }
        // history heuristic
        for(int i = 0; i < dependentTable.length; i++){
            dependentTable[i] += Math.round(historyTable[i] * 1.0 / maxHistoryValue * 20);
        }

        // add reach moves
        LocationList fourThreats = threatMapList[G3Constants.FOUR_THREAT_INDEX];
        LocationList threeThreats = threatMapList[G3Constants.THREE_THREAT_INDEX];
        for(int fourThreatIndex = 0; fourThreatIndex < fourThreats.getSize(); fourThreatIndex++){
            int move = fourThreats.getLocation(fourThreatIndex);
            if((threeThreats.containsLocation(move) || fourThreats.getLocationInstances(move) > 1) && !moves.contains(move)){
                moves.add(move);
                dependentTable[move] += 5;
            }
        }
        fourThreats = threatMapList[G3Constants.FOUR_THREAT_INDEX + 4];
        threeThreats = threatMapList[G3Constants.THREE_THREAT_INDEX + 4];
        for(int fourThreatIndex = 0; fourThreatIndex < fourThreats.getSize(); fourThreatIndex++){
            int move = fourThreats.getLocation(fourThreatIndex);
            if((threeThreats.containsLocation(move) || fourThreats.getLocationInstances(move) > 1) && !moves.contains(move)){
                moves.add(move);
                dependentTable[move] += 5;
            }
        }

        for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
            int move = moves.get(moveIndex);
            if(threatMapList[G3Constants.THREE_THREAT_INDEX].containsLocation(move)){
                dependentTable[move] += 
                    5 * (Math.pow(threatMapList[G3Constants.THREE_THREAT_INDEX].getLocationInstances(move), 2));
            }
            if(threatMapList[G3Constants.THREE_THREAT_INDEX + 4].containsLocation(move)){
                dependentTable[move] += 
                    5 * (Math.pow(threatMapList[G3Constants.THREE_THREAT_INDEX + 4].getLocationInstances(move), 2));
            }
            if(threatMapList[G3Constants.FOUR_THREAT_INDEX].containsLocation(move)){
                dependentTable[move] += 
                    30 * (Math.pow(threatMapList[G3Constants.FOUR_THREAT_INDEX].getLocationInstances(move), 2));
            }
            if(threatMapList[G3Constants.FOUR_THREAT_INDEX + 4].containsLocation(move)){
                dependentTable[move] += 
                    30 * (Math.pow(threatMapList[G3Constants.FOUR_THREAT_INDEX + 4].getLocationInstances(move), 2));
            }
            if(threatMapList[G3Constants.OPEN_FOUR_THREAT_INDEX].containsLocation(move)){
                dependentTable[move] += 120;
            }
            if(threatMapList[G3Constants.OPEN_FOUR_THREAT_INDEX + 4].containsLocation(move)){
                dependentTable[move] += 120;
            }
            if(threatMapList[G3Constants.FIVE_THREAT_INDEX].containsLocation(move)){
                dependentTable[move] += 10000;
            }
            if(threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)){
                dependentTable[move] += 10000;
            }
        }
        Collections.sort(moves, dependentCompare);

        // finally, highest priority, table hash move
        TTEntry entry = table.get(hash);
        if(table.get(hash) != null){
            int move = entry.getBestMove();
            if(myVirtualBoard.isMoveValid(move)){
                moves.remove(Integer.valueOf(move));
                moves.add(0, move);
            }
        }
    }

    public SuperMove minimax(int originalDepth, int depth, boolean isMaximizingPlayer, int movePlayed,
     int generation, double alpha, double beta) throws TimeoutException{
        nodes++;
        if((nodes % 100) == 0 && System.nanoTime() - startTime >= timeToPlay * 1000000000){
            throw new TimeoutException();
        }
        // check transposition table first to see minimax can be skipped for this node
        long hash = Zobrist.computeHash(myVirtualBoard);
        // long recomputedHash = Zobrist.computeHash(myVirtualBoard);
        // if(hash != recomputedHash) {
        //     System.out.println("HASH MISMATCH at start of minimax!");
        //     System.out.println("Maintained: " + hash);
        //     System.out.println("Recomputed: " + recomputedHash);
        // }
        TTEntry currentEntry = table.get(hash);
        if(currentEntry != null){
            int tableMove = currentEntry.getBestMove();
            if(currentEntry.getDepth() >= depth){
                TThits++;
                switch(currentEntry.getType()){
                    case TTEntry.EXACT:
                        if(originalDepth != depth)
                            return new SuperMove(currentEntry.getBestMove(), currentEntry.getEval());
                        break;
                    case TTEntry.LOWER_BOUND:
                        if(currentEntry.getEval() >= beta && originalDepth != depth) {
                            prunes++;
                            return new SuperMove(currentEntry.getBestMove(), currentEntry.getEval());
                        }
                        alpha = Math.max(alpha, currentEntry.getEval());
                        break;

                    case TTEntry.UPPER_BOUND:
                        if(currentEntry.getEval() <= alpha && originalDepth != depth) {
                            prunes++;
                            return new SuperMove(currentEntry.getBestMove(), currentEntry.getEval());
                        }
                        beta = Math.min(beta, currentEntry.getEval());
                        break;
                }
            }
        }

        int thisPositionEval = myVirtualBoard.updateEvaluation(movePlayed);
        if(depth == 0 || Math.abs(thisPositionEval) >= (G3Constants.GAME_WILL_BE_OVER) || thisPositionEval == G3Constants.GAME_DRAWN){
            if(originalDepth != depth){
                if(Math.abs(thisPositionEval) == G3Constants.GAME_OVER){
                    thisPositionEval += (Math.signum(thisPositionEval) * (20 + depth * 3)); // prioritizes quick win
                }
                else if(Math.abs(thisPositionEval) == G3Constants.GAME_WILL_BE_OVER){
                    prunes++;
                    thisPositionEval += (Math.signum(thisPositionEval) * depth * 1); // prioritizes quick win
                }
                else if(thisPositionEval >= G3Constants.GAME_DRAWN){
                    thisPositionEval = 0;
                }
                // }else if(depth == 0 && fix){
                //     thisPositionEval = (int)quiescenceSearch(
                //         QUIESCENCE_DEPTH_LIMIT, QUIESCENCE_DEPTH_LIMIT, isMaximizingPlayer, 
                //         movePlayed, alpha, beta).getScore();
                // }

                table.add(hash, new TTEntry(thisPositionEval, depth, TTEntry.EXACT, -1, generation, hash));
                return new SuperMove(-1, thisPositionEval);
            }
        }

        // Futility Pruning
        if(depth == 1 && depth != originalDepth && Math.abs(thisPositionEval) < 1000){
            int margin = 150;
            if(isMaximizingPlayer && thisPositionEval + margin < alpha){
                return new SuperMove(-1, thisPositionEval);
            }else if(!isMaximizingPlayer && thisPositionEval - margin > beta){
                return new SuperMove(-1, thisPositionEval);
            }
        }

        ArrayList<Integer> moves = myVirtualBoard.updateCandidateMoves(movePlayed);
        LocationList[] threatMapList = myVirtualBoard.fetchThreatMapList();
        boolean testCondition;
        int plyFromRoot = originalDepth - depth;
        int moveEnd = moves.size();
        // Pattern-based move trimming
        if((threatMapList[G3Constants.FIVE_THREAT_INDEX].getSize() > 0 ||
         threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].getSize() > 0)){
            moveEnd = threatMapList[G3Constants.FIVE_THREAT_INDEX].getSize()
                + threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].getSize() + 1;
        }else if((threatMapList[G3Constants.OPEN_FOUR_THREAT_INDEX].getSize() > 0
        || threatMapList[G3Constants.OPEN_FOUR_THREAT_INDEX + 4].getSize() > 0)){
            moveEnd = threatMapList[G3Constants.FOUR_THREAT_INDEX].getSize() 
                + threatMapList[G3Constants.FOUR_THREAT_INDEX  + 4].getSize() + 1;
        }
        moveEnd = Math.min(moveEnd, moves.size());
        if(isMaximizingPlayer){
            SuperMove bestMove = new SuperMove(-1, Double.NEGATIVE_INFINITY);
            orderMoves(moves, threatMapList, hash, plyFromRoot, false);
            if(searchThisMoveFirst && depth == originalDepth){
                TTEntry entry = table.get(hash);
                testCondition = (entry == null || entry.getDepth() < originalDepth);
                if(testCondition){
                    moves.remove(Integer.valueOf(bestMoveFound.getMoveLocation()));
                    moves.add(0, bestMoveFound.getMoveLocation());
                }
            }
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moveEnd; moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex),
                 generation, newAlpha, newBeta).getScore();
                myVirtualBoard.undoStone();
                myVirtualBoard.setCandidateMoves(moves);
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore > bestMove.getScore()){
                    bestMove.set(moves.get(moveIndex), newestScore);
                    if(originalDepth == depth)
                        bestMoveFound = bestMove;
                }
                if(bestMove.getScore() > newAlpha)
                    newAlpha = bestMove.getScore();
                if(newBeta <= newAlpha){ //fail high - found something too good
                    prunes++;
                    int move = bestMove.getMoveLocation();
                    historyTable[move] += depth * depth;
                    table.add(hash, new TTEntry((int)bestMove.getScore(), depth, TTEntry.LOWER_BOUND, bestMove.getMoveLocation(), generation, hash));
                    return bestMove;
                }
            }
            if(bestMove.getScore() <= alpha){   // fail low - didn't find something better
                table.add(hash, new TTEntry((int)bestMove.getScore(), depth, TTEntry.UPPER_BOUND, bestMove.getMoveLocation(), generation, hash));
            }else{
                table.add(hash, new TTEntry((int)bestMove.getScore(), depth, TTEntry.EXACT, bestMove.getMoveLocation(), generation, hash));
            }
            return bestMove;
        }

        else{   // Minimizing Player
            SuperMove bestMove = new SuperMove(-1, Double.POSITIVE_INFINITY);
            orderMoves(moves, threatMapList, hash, plyFromRoot, true);
            if(searchThisMoveFirst && depth == originalDepth){
                TTEntry entry = table.get(hash);
                testCondition = (entry == null || entry.getDepth() < originalDepth);
                if(testCondition){
                    moves.remove(Integer.valueOf(bestMoveFound.getMoveLocation()));
                    moves.add(0, bestMoveFound.getMoveLocation());
                }
            }
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moveEnd; moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex), 
                    generation, newAlpha, newBeta).getScore();
                myVirtualBoard.undoStone();
                myVirtualBoard.setCandidateMoves(moves);
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore < bestMove.getScore()){
                    bestMove.set(moves.get(moveIndex), newestScore);
                    if(originalDepth == depth)
                        bestMoveFound = bestMove;
                }
                if(bestMove.getScore() < newBeta)
                    newBeta = bestMove.getScore();
                if(newBeta <= newAlpha){    // fail high - move too good
                    prunes++;
                    int move = bestMove.getMoveLocation();
                    historyTable[move] += depth * depth;
                    table.add(hash, new TTEntry((int)bestMove.getScore(), depth, TTEntry.UPPER_BOUND, bestMove.getMoveLocation(), generation, hash));
                    return bestMove;
                }
            }
            //System.out.println("Depth " + depth + ": " + bestMove.getMoveLocation());
            if(bestMove.getScore() >= beta){   // fail low - didn't find something better
                table.add(hash, new TTEntry((int)bestMove.getScore(), depth, TTEntry.LOWER_BOUND, bestMove.getMoveLocation(), generation, hash));
            }else{
                table.add(hash, new TTEntry((int)bestMove.getScore(), depth, TTEntry.EXACT, bestMove.getMoveLocation(), generation, hash));
            }
            return bestMove;
        }

    }

    private SuperMove quiescenceSearch(int originalDepth, int depth, 
        boolean isMaximizingPlayer, int movePlayed, double alpha, double beta) throws TimeoutException{
        nodes++;
        if((nodes % 100) == 0 && System.nanoTime() - startTime >= timeToPlay * 1000000000){
            throw new TimeoutException();
        }
        // check transposition table first to see minimax can be skipped for this node
        long hash = Zobrist.computeHash(myVirtualBoard);
  
        TTEntry currentEntry = table.get(hash);
        if(currentEntry != null){
            int tableMove = currentEntry.getBestMove();
            if(currentEntry.getDepth() >= depth){
                TThits++;
                switch(currentEntry.getType()){
                    case TTEntry.EXACT:
                        return new SuperMove(currentEntry.getBestMove(), currentEntry.getEval());
                    case TTEntry.LOWER_BOUND:
                        if(currentEntry.getEval() >= beta) {
                            return new SuperMove(currentEntry.getBestMove(), currentEntry.getEval());
                        }
                        alpha = Math.max(alpha, currentEntry.getEval());
                        break;

                    case TTEntry.UPPER_BOUND:
                        if(currentEntry.getEval() <= alpha) {
                            return new SuperMove(currentEntry.getBestMove(), currentEntry.getEval());
                        }
                        beta = Math.min(beta, currentEntry.getEval());
                        break;
                }
            }
        }

        int thisPositionEval = myVirtualBoard.updateEvaluation(movePlayed);
        ArrayList<Integer> moves = getQuiescenceMoves(hash);
        //ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
        if(depth == 0 || Math.abs(thisPositionEval) >= (G3Constants.GAME_WILL_BE_OVER) || thisPositionEval == G3Constants.GAME_DRAWN){
            if(Math.abs(thisPositionEval) == G3Constants.GAME_OVER){
                thisPositionEval += (Math.signum(thisPositionEval) * (20 + depth * 3)); // prioritizes quick win
            }
            else if(Math.abs(thisPositionEval) == G3Constants.GAME_WILL_BE_OVER){
                thisPositionEval += (Math.signum(thisPositionEval) * depth * 1); // prioritizes quick win
            }
            else if(thisPositionEval >= G3Constants.GAME_DRAWN){
                thisPositionEval = 0;
            }

            if(depth == 0 && moves.size() != 0){    // forced to stop quiescence search early = penalty
                if(isOpponentBlack)
                    thisPositionEval -= UNCERTAINTY_PENALTY;
                else
                    thisPositionEval += UNCERTAINTY_PENALTY;
            }
            return new SuperMove(-1, thisPositionEval);
        }

        // if no more quiescent moves, stop the search
        if(moves.size() == 0){
            return new SuperMove(-1, thisPositionEval);
        }
        LocationList[] threatMapList = myVirtualBoard.fetchThreatMapList();
        orderMoves(moves, threatMapList, hash, originalDepth - depth, true);

        // Stand pat code
        // assumes currentEvaluation is a lower bound for what current player can achieve
        // if currentEvaluation is already too good for the opponent to allow, prune
        // if(isMaximizingPlayer){
        //     if(thisPositionEval - SAFETY_MARGIN >= beta){
        //         return new SuperMove(-1, thisPositionEval);
        //     }
        // }else{  // isMinimizingPlayer
        //     if(thisPositionEval + SAFETY_MARGIN <= alpha){
        //         return new SuperMove(-1, thisPositionEval);
        //     }
        // }

        if(isMaximizingPlayer){
            SuperMove bestMove = new SuperMove(-1, Double.NEGATIVE_INFINITY);
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = quiescenceSearch(originalDepth, depth - 1, !isMaximizingPlayer,
                 moves.get(moveIndex), newAlpha, newBeta).getScore();
                myVirtualBoard.undoStone();
                //myVirtualBoard.setCandidateMoves(moves);
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore > bestMove.getScore()){
                    bestMove.set(moves.get(moveIndex), newestScore);
                }
                if(bestMove.getScore() > newAlpha)
                    newAlpha = bestMove.getScore();
                if(newBeta <= newAlpha){ //fail high - found something too good
                    return bestMove;
                }
            }
            return bestMove;
        }

        else{   // Minimizing Player
            SuperMove bestMove = new SuperMove(-1, Double.POSITIVE_INFINITY);
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = quiescenceSearch(originalDepth, depth - 1, !isMaximizingPlayer,
                 moves.get(moveIndex), newAlpha, newBeta).getScore();
                myVirtualBoard.undoStone();
                //myVirtualBoard.setCandidateMoves(moves);
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore < bestMove.getScore()){
                    bestMove.set(moves.get(moveIndex), newestScore);
                }
                if(bestMove.getScore() < newBeta)
                    newBeta = bestMove.getScore();
                if(newBeta <= newAlpha){    // fail high - move too good
                    return bestMove;
                }
            }
            return bestMove;
        }
    }

    public ArrayList<Integer> getQuiescenceMoves(long hash){
        ArrayList<Integer> qMoves = new ArrayList<Integer>();
        LocationList[] threatMapList = myVirtualBoard.fetchThreatMapList();
        // first add 5-threats
        for(int i = G3Constants.FIVE_THREAT_INDEX; i < 5; i += 4){
            for(int moveIndex = 0; moveIndex < threatMapList[i].getSize(); moveIndex++){
                qMoves.add(threatMapList[i].getLocation(moveIndex));
            }
        }
        // then add 4-threats
        for(int i = G3Constants.FOUR_THREAT_INDEX; i < 7; i += 4){
            for(int moveIndex = 0; moveIndex < threatMapList[i].getSize(); moveIndex++){
                qMoves.add(threatMapList[i].getLocation(moveIndex));
            }
        }
        //add double 3-threats
        for(int i = G3Constants.THREE_THREAT_INDEX; i < 8; i += 4){
            for(int moveIndex = 0; moveIndex < threatMapList[i].getSize(); moveIndex++){
                int move = threatMapList[i].getLocation(moveIndex);
                if(threatMapList[i].getLocationInstances(move) > 1)
                    qMoves.add(threatMapList[i].getLocation(moveIndex));
            }
        }
        // order table move first if existent
        TTEntry entry = table.get(hash);
        if(table.get(hash) != null){
            int move = entry.getBestMove();
            if(myVirtualBoard.isMoveValid(move)){
                qMoves.remove(Integer.valueOf(move));
                qMoves.add(0, move);
            }
        }
        return qMoves;
    }

    public boolean equals(Object o){
        if(o instanceof G3VirtualBoard){
            G3VirtualBoard other = (G3VirtualBoard) o;
            if(super.equals(other) && 
               this.myVirtualBoard.getMoveHistory().equals(other.getMoveHistory())){
                return true;
            }
        }
        return false;
    }

}
