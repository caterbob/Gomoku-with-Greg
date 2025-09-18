package GomokuAIproject.Greg3;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import GomokuAIproject.Board;
import GomokuAIproject.Engine;
import GomokuAIproject.EngineHelpers.SuperMove;

public class Greg3 implements Engine{

    private G3VirtualBoard myVirtualBoard;
    private boolean isOpponentBlack;
    private TranspositionTable table;
    private KillerMoveTable killerMoves;
    private double timeToPlay;
    private int totalDepth;

    // stats
    private long startTime;
    private int TThits;
    private int nodes;
    private int prunes;
    private double movesPlayed;
    private int leafNodes;
    private double totalNodes;

    private int generation;
    private boolean fix;

    private boolean searchThisMoveFirst;
    private SuperMove bestMoveFound;

    private static final int SAFETY_MARGIN = 50;
    private static final int UNCERTAINTY_PENALTY = 50;
    private static final int QUIESCENCE_DEPTH_LIMIT = 4;
    private static final int DEPTH_LIMIT = 15;
    private static final int KILLER_MOVES_STORED = 3;

    
    public Greg3(boolean isOpponentBlack, double timeToPlay, boolean testSegments, boolean fix){
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard = new G3VirtualBoard(isOpponentBlack, testSegments, fix);
        table = new TranspositionTable(G3Constants.TTSize);
        killerMoves = new KillerMoveTable(DEPTH_LIMIT, 3);
        this.timeToPlay = timeToPlay;
        generation = 0;
        nodes = 0;
        totalDepth = 0;
        movesPlayed = 0;
        this.fix = fix;
        totalTimeLeft = 295;
        EFBtotal = 0;
        totalGenerations = 0;
        totalNodes = 0;
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        // lastEvaluation = 0;
        // currentEvaluation = 0;
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard.setIsOpponentBlack(isOpponentBlack);
        table.clear();
        killerMoves.clear();
        generation = 0;
        //System.out.println("Fix? " + fix + ", Average Depth: " + totalDepth / movesPlayed);
        System.out.println("Average Nodes Per Second (" + fix + "): "  + (int)((nodes / movesPlayed) / timeToPlay) + " N/s");
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
        // System.out.println("--------------");
        // System.out.println("Move Played: " + bestMoveFound.getMoveLocation());
        // System.out.println("Depth reached: " + (finalDepth));
        // //System.out.println("Evaluation: " + bestMoveFound.getScore());
        // System.out.println("Positions searched: " + ((int)(nodes / 10000)/100.0) + "M");
        // int evaluation = (int)bestMoveFound.getScore();
        // int mySign = (isOpponentBlack)? 1: -1;
        // evaluation *= mySign;
        // if(evaluation <= -90000){
        //     System.out.println("I'm dead");
        // }else if(evaluation <= -220){
        //     System.out.println("Strongly unfavorable");
        // }else if(evaluation < -120){
        //     System.out.println("Unfavorable");
        // }else if(evaluation <= 120){
        //     System.out.println("Neutral");
        // }else if(evaluation < 220){
        //     System.out.println("Favorable");
        // }else if(evaluation < 90000){
        //     System.out.println("Strongly favorable");
        // }else{
        //     System.out.println("You're dead");
        // }
        totalNodes += (nodes * 1.0)/1000000.0;
        System.out.println("Fix: " + fix + " avg nM/p: " + totalNodes / totalGenerations);
        // lastEval = currentEval;
        // currentEval = (int)bestMoveFound.getScore() * mySign;
        // if(lastEval > 90000 && currentEval < 90000){
        //     System.out.println("opponent black: " + isOpponentBlack);
        //     System.out.println("next move: " + myMove);
        //     System.out.println("last eval: " + lastEval);
        //     System.out.println("c eval: " + currentEval);
        //     int a = 1 / 0;
        // }
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
        generation++;
        movesPlayed++;
        myVirtualBoard.sync();  // syncs to current board state and clears move history
        myVirtualBoard.getEvaluation();
        //table.clear();
        TThits = 0;
        //nodes = 0;
        prunes = 0;
        ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
        bestMoveFound = new SuperMove(moves.get(0), 0);
        searchThisMoveFirst = false;
        // bring old killer moves one ply closer to root for new iteration
        killerMoves.shiftBackward();
        startTime = System.nanoTime();
        int finalDepth = 1;
        for (int depth = 1; depth <= DEPTH_LIMIT; depth++) {
            try {
                bestMoveFound = minimax(depth, isOpponentBlack, board.getLastMove(),
                    generation);
                searchThisMoveFirst = true;
            } catch (TimeoutException e) {
                //System.out.println("Depth reached (partial): " + (depth));
                //System.out.println("Evaluation: " + bestMoveFound.getScore());
                //System.out.println("Positions searched: " + nodes);
                //System.out.println("Fix?: " + fix + ", Total Depth " + totalDepth);
                break; // fallback to last bestMove
            }
            finalDepth = depth;
            if(depth == DEPTH_LIMIT){
                //System.out.println("Fix?: " + fix + ", Total Depth " + totalDepth);
            }
        }
        totalDepth += finalDepth;
        int myMove = bestMoveFound.getMoveLocation();
        // System.out.println("Nodes: " + nodes);
        // System.out.println("TTHits: " + TThits);
        //System.out.println(myMove);
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
                bestMoveFound = minimax(depth, isOpponentBlack, board.getLastMove(),
                    generation);
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
        //System.out.println("Fix? " + fix + " EFB avg: " + EFBtotal / totalGenerations);
        return finalDepth;
    }

    // also adds reach moves (gap of 1 if also a threat of some kind)
    private void orderMoves(ArrayList<Integer> moves, LocationList[] threatMapList, long hash, int plyFromRoot){
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
                dependentTable[move] += 10000000;
            }
            if(threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)){
                dependentTable[move] += 10000000;
            }
        }
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

    // intended for root
    public SuperMove minimax(int depth, boolean isMaximizingPlayer,
     int movePlayed, int generation) throws TimeoutException{
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        nodes++;
        if((nodes % 1000) == 0 && System.nanoTime() - startTime >= timeToPlay * 1000000000){
            throw new TimeoutException();
        }
        // check transposition table first to see minimax can be skipped for this node
        long hash = myVirtualBoard.getCurrentHash();
       
        TTEntry currentEntry = table.get(hash);
        if(currentEntry != null){
            int tableMove = currentEntry.getBestMove();
            if(currentEntry.getDepth() >= depth){
                TThits++;
                switch(currentEntry.getType()){
                    case TTEntry.LOWER_BOUND:
                        alpha = Math.max(alpha, currentEntry.getEval());
                        break;
                    case TTEntry.UPPER_BOUND:
                        beta = Math.min(beta, currentEntry.getEval());
                        break;
                }
            }
        }

        int thisPositionEval = myVirtualBoard.updateEvaluation(movePlayed);

        ArrayList<Integer> moves = myVirtualBoard.updateCandidateMoves(movePlayed);
        LocationList[] threatMapList = myVirtualBoard.fetchThreatMapList();
        boolean testCondition;
        int plyFromRoot = 0;
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
            orderMoves(moves, threatMapList, hash, plyFromRoot);
            if(searchThisMoveFirst){
                TTEntry entry = table.get(hash);
                testCondition = (entry == null || entry.getDepth() < depth);
                if(testCondition){
                    moves.remove(Integer.valueOf(bestMoveFound.getMoveLocation()));
                    moves.add(0, bestMoveFound.getMoveLocation());
                }
            }
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = (int)minimax(depth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex),
                 generation, newAlpha, newBeta);
                myVirtualBoard.undoStone();
                myVirtualBoard.setCandidateMoves(moves);
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore > bestMove.getScore()){
                    bestMove.set(moves.get(moveIndex), newestScore);
                    bestMoveFound = bestMove;
                }
                if(bestMove.getScore() > newAlpha)
                    newAlpha = bestMove.getScore();
                if(newBeta <= newAlpha){ //fail high - found something too good
                    prunes++;
                    int move = bestMove.getMoveLocation();
                    // if(fix && !threatMapList[G3Constants.FIVE_THREAT_INDEX].containsLocation(move)
                    // && !threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)){
                    //     killerMoves.addKillerMove(move, depth);
                    // }
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
            orderMoves(moves, threatMapList, hash, plyFromRoot);
            if(searchThisMoveFirst){
                TTEntry entry = table.get(hash);
                testCondition = (entry == null || entry.getDepth() < depth);
                if(testCondition){
                    moves.remove(Integer.valueOf(bestMoveFound.getMoveLocation()));
                    moves.add(0, bestMoveFound.getMoveLocation());
                }
            }
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(depth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex), 
                    generation, newAlpha, newBeta);
                myVirtualBoard.undoStone();
                myVirtualBoard.setCandidateMoves(moves);
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore < bestMove.getScore()){
                    bestMove.set(moves.get(moveIndex), newestScore);
                    bestMoveFound = bestMove;
                }
                if(bestMove.getScore() < newBeta)
                    newBeta = bestMove.getScore();
                if(newBeta <= newAlpha){    // fail high - move too good
                    prunes++;
                    int move = bestMove.getMoveLocation();
                    // if(fix && !threatMapList[G3Constants.FIVE_THREAT_INDEX].containsLocation(move)
                    // && !threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)){
                    //     killerMoves.addKillerMove(move, depth);
                    // }
                    table.add(hash, new TTEntry((int)bestMove.getScore(), depth, TTEntry.UPPER_BOUND, bestMove.getMoveLocation(), generation, hash));
                    return bestMove;
                }
            }
            if(bestMove.getScore() >= beta){   // fail low - didn't find something better
                table.add(hash, new TTEntry((int)bestMove.getScore(), depth, TTEntry.LOWER_BOUND, bestMove.getMoveLocation(), generation, hash));
            }else{
                table.add(hash, new TTEntry((int)bestMove.getScore(), depth, TTEntry.EXACT, bestMove.getMoveLocation(), generation, hash));
            }
            return bestMove;
        }

    }

    // NOT for root
    public double minimax(int originalDepth, int depth, boolean isMaximizingPlayer, int movePlayed,
     int generation, double alpha, double beta) throws TimeoutException{
        nodes++;
        if((nodes % 1000) == 0 && System.nanoTime() - startTime >= timeToPlay * 1000000000){
            throw new TimeoutException();
        }
        // check transposition table first to see minimax can be skipped for this node
        long hash = myVirtualBoard.getCurrentHash();
       
        TTEntry currentEntry = table.get(hash);
        if(currentEntry != null){
            int tableMove = currentEntry.getBestMove();
            if(currentEntry.getDepth() >= depth){
                TThits++;
                switch(currentEntry.getType()){
                    case TTEntry.EXACT:
                        return currentEntry.getEval();
                    case TTEntry.LOWER_BOUND:
                        if(currentEntry.getEval() >= beta) {
                            prunes++;
                            return currentEntry.getEval();
                        }
                        alpha = Math.max(alpha, currentEntry.getEval());
                        break;

                    case TTEntry.UPPER_BOUND:
                        if(currentEntry.getEval() <= alpha) {
                            prunes++;
                            return currentEntry.getEval();
                        }
                        beta = Math.min(beta, currentEntry.getEval());
                        break;
                }
            }
        }

        int thisPositionEval = myVirtualBoard.updateEvaluation(movePlayed);
        if(depth == 0 || Math.abs(thisPositionEval) >= (G3Constants.GAME_WILL_BE_OVER) || thisPositionEval == G3Constants.GAME_DRAWN){
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

            table.add(hash, new TTEntry(thisPositionEval, depth, TTEntry.EXACT, -1, generation, hash));
            return thisPositionEval;
        }

        // Futility Pruning
        if(depth == 1 && Math.abs(thisPositionEval) < 1000){
            int margin = 150;
            if(isMaximizingPlayer && thisPositionEval + margin < alpha){
                return thisPositionEval;
            }else if(!isMaximizingPlayer && thisPositionEval - margin > beta){
                return thisPositionEval;
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
            double bestScore = Double.NEGATIVE_INFINITY;
            int bestMove = moves.get(0);
            orderMoves(moves, threatMapList, hash, plyFromRoot);
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moveEnd; moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex),
                 generation, newAlpha, newBeta);
                myVirtualBoard.undoStone();
                myVirtualBoard.setCandidateMoves(moves);
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore > bestScore){
                    bestScore = newestScore;
                    bestMove = moves.get(moveIndex);
                }
                if(bestScore > newAlpha)
                    newAlpha = bestScore;
                if(newBeta <= newAlpha){ //fail high - found something too good
                    prunes++;
                    historyTable[bestMove] += depth * depth;
                    table.add(hash, new TTEntry((int)bestScore, depth, TTEntry.LOWER_BOUND, bestMove, generation, hash));
                    return bestScore;
                }
            }
            if(bestScore <= alpha){   // fail low - didn't find something better
                table.add(hash, new TTEntry((int)bestScore, depth, TTEntry.UPPER_BOUND, bestMove, generation, hash));
            }else{
                table.add(hash, new TTEntry((int)bestScore, depth, TTEntry.EXACT, bestMove, generation, hash));
            }
            return bestScore;
        }

        else{   // Minimizing Player
            double bestScore = Double.POSITIVE_INFINITY;
            int bestMove = moves.get(0);
            orderMoves(moves, threatMapList, hash, plyFromRoot);
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moveEnd; moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex), 
                    generation, newAlpha, newBeta);
                myVirtualBoard.undoStone();
                myVirtualBoard.setCandidateMoves(moves);
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore < bestScore){
                    bestScore = newestScore;
                    bestMove = moves.get(moveIndex);
                }
                if(bestScore < newBeta)
                    newBeta = bestScore;
                if(newBeta <= newAlpha){    // fail high - move too good
                    prunes++;
                    historyTable[bestMove] += depth * depth;
                    table.add(hash, new TTEntry((int)bestScore, depth, TTEntry.UPPER_BOUND, bestMove, generation, hash));
                    return bestScore;
                }
            }
            //System.out.println("Depth " + depth + ": " + bestMove.getMoveLocation());
            if(bestScore >= beta){   // fail low - didn't find something better
                table.add(hash, new TTEntry((int)bestScore, depth, TTEntry.LOWER_BOUND, bestMove, generation, hash));
            }else{
                table.add(hash, new TTEntry((int)bestScore, depth, TTEntry.EXACT, bestMove, generation, hash));
            }
            return bestScore;
        }

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
