package GomokuAIproject.Greg3;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import GomokuAIproject.Board;
import GomokuAIproject.Engine;
import GomokuAIproject.EngineHelpers.SuperMove;

public class Greg3 implements Engine{

    private G3VirtualBoard[] myVirtualBoards;
    private boolean isOpponentBlack;
    private int depth;
    private TranspositionTable table;
    private KillerMoveTable killerMoves;
    private double timeToPlay;
    private double OGtimeToPlay;
    private double totalTimeLeft;
    private int totalDepth;

    // stats
    private long startTime;
    private int TThits;
    private int nodes;
    private int prunes;
    private double movesPlayed;

    private int generation;
    private boolean fix;

    private boolean[] searchThisMoveFirst;
    private SuperMove[] bestMovesFound;
    private ArrayList<Integer> candidateBestMoves;

    private static final int SAFETY_MARGIN = 50;
    private static final int UNCERTAINTY_PENALTY = 50;
    private static final int QUIESCENCE_DEPTH_LIMIT = 4;
    private static final int DEPTH_LIMIT = 15;
    private static final int KILLER_MOVES_STORED = 3;
    private static int THREADS_USED;

    
    public Greg3(boolean isOpponentBlack, double timeToPlay, boolean testSegments, boolean fix){
        this.isOpponentBlack = isOpponentBlack;
        THREADS_USED = (fix)? 4: 1;
        myVirtualBoards = new G3VirtualBoard[THREADS_USED];
        for(int i = 0; i < myVirtualBoards.length; i++){
            myVirtualBoards[i] = new G3VirtualBoard(isOpponentBlack, testSegments, fix);
        }
        table = new TranspositionTable(G3Constants.TTSize);
        killerMoves = new KillerMoveTable(DEPTH_LIMIT, 3);
        this.timeToPlay = timeToPlay;
        OGtimeToPlay = timeToPlay;
        generation = 0;
        nodes = 0;
        totalDepth = 0;
        movesPlayed = 0;
        this.fix = fix;
        totalTimeLeft = 295;
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        // lastEvaluation = 0;
        // currentEvaluation = 0;
        this.isOpponentBlack = isOpponentBlack;
        for(G3VirtualBoard virtualBoard: myVirtualBoards){
            virtualBoard.setIsOpponentBlack(isOpponentBlack);
        }
        table.clear();
        generation = 0;
        //System.out.println("Fix? " + fix + ", Average Depth: " + totalDepth / movesPlayed);
        System.out.println("Average Nodes Per Second: " + (int)((nodes / movesPlayed) / timeToPlay) + " N/s");
    }

    public boolean getIsOpponentBlack(){
        return isOpponentBlack;
    }

    public int playFromPosition(Board board){

        generation++;
        movesPlayed++;

        for(G3VirtualBoard virtualBoard: myVirtualBoards){
            virtualBoard.sync();  // syncs to current board state and clears move history
            virtualBoard.getEvaluation();
        }

        TThits = 0;
        nodes = 0;
        prunes = 0;

        ArrayList<Integer> moves = myVirtualBoards[0].getCandidateMoves();
        bestMovesFound = new SuperMove[THREADS_USED];
        for(int i = 0; i < bestMovesFound.length; i++){
            bestMovesFound[i] = new SuperMove(-1, -1);
        }
        searchThisMoveFirst = new boolean[THREADS_USED];
        int[] finalDepths = new int[THREADS_USED];

        ExecutorService executor = Executors.newFixedThreadPool(THREADS_USED);
        Runnable[] threads = new Runnable[THREADS_USED];
        for(int i = 0; i < threads.length; i++){
            final int ID = i;
            threads[ID] = () -> {
                    finalDepths[ID] = iterativeSearch(DEPTH_LIMIT, board, ID);
            };
        }

        Future<?>[] futures = new Future<?>[threads.length];
        for(int i = 0; i < threads.length; i++){
            futures[i] = executor.submit(threads[i]);
        }

        // waits for threads to finish
        for(int i = 0; i < threads.length; i++){
            try{
                futures[i].get();
            } catch(InterruptedException e){
                System.out.println("Interesting");
            } catch(ExecutionException e){
                System.out.println(e.getCause());
                Throwable cause = e.getCause();
                cause.printStackTrace();
                System.out.println("Bad error");
            }
        }

        SuperMove overallBestMove = new SuperMove(-1, 0);
        int bestDepth = Integer.MIN_VALUE;
        int bestIndex = 0;
        int mySign = (isOpponentBlack)? 1: -1;
        int bestScore = Integer.MAX_VALUE * mySign * -1;
        for(int i = 0; i < finalDepths.length; i++){
            System.out.println(bestMovesFound[i].getMoveLocation());
            if(finalDepths[i] > bestDepth || 
            (finalDepths[i] == bestDepth && bestMovesFound[i].getScore() * mySign > bestScore)){
                bestDepth = finalDepths[i];
                bestScore = (int)Math.abs(bestMovesFound[i].getScore());
                bestIndex = i;
            }
        }
        overallBestMove = bestMovesFound[bestIndex];
        
        int myMove = overallBestMove.getMoveLocation();
        // System.out.println("--------------");
        // System.out.println("Move Played: " + overallBestMove.getMoveLocation());
        // System.out.println("Evaluation: " + overallBestMove.getScore());
        // System.out.println("Positions searched: " + nodes);
        // System.out.println("Depth reached: " + (finalDepths[bestIndex]));
        

        return myMove;

    }

    public int playFromPositionHuman(Board board){
        return playFromPosition(board);
    }
    // plays in a human-like manner
    // boolean foundWin;
    // boolean previouslyFoundWin;
    // double timeTakenThisTurn = 0;
    // boolean playedForcingMove = false;
    // public int playFromPositionHuman(Board board){
    //     // if(!play){
    //     //     return -1;
    //     // }
    //     if(generation == 0){
    //         foundWin = false;
    //         previouslyFoundWin = false;
    //     }
    //     foundWin = false;
    //     generation++;
    //     movesPlayed++;
    //     myVirtualBoard.sync();  // syncs to current board state and clears move history
    //     int evaluation = myVirtualBoard.getEvaluation();
    //     if(bestMoveFound != null){
    //         evaluation = (int)bestMoveFound.getScore();
    //     }
    //     //table.clear();
    //     TThits = 0;
    //     nodes = 0;
    //     prunes = 0;
    //     ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
    //     LocationList[] threatMapList = myVirtualBoard.fetchThreatMapList();
    //     bestMoveFound = new SuperMove(moves.get(0), 0);
    //     SuperMove lastBestMove = new SuperMove(-1, -1);
    //     searchThisMoveFirst = false;
    //     // bring old killer moves one ply closer to root for new iteration
    //     killerMoves.shiftBackward();
    //     startTime = System.nanoTime();

    //     // determine how much time to spend
    //     int offset = (isOpponentBlack)? 0: 4;
    //     int offset2 = (isOpponentBlack)? 4: 0;
    //     boolean beingForced = threatMapList[G3Constants.FIVE_THREAT_INDEX + offset].getSize() > 0;
    //     if(beingForced){
    //         timeToPlay = 0.5;
    //     }else{
    //         timeToPlay = (moves.size() / 40.0) * OGtimeToPlay * (1 + Math.random() * 0.4 - 0.2);
    //         if(myVirtualBoard.getMoveHistory().size() < 4){
    //             timeToPlay *= 0.1;
    //         }
    //     }
    //     int mySign = (isOpponentBlack)? 1: -1;
    //     if(Math.abs(evaluation) < 100){
    //         System.out.println("neutral");
    //         timeToPlay *= 1;
    //     }else if(evaluation * mySign >= 150){
    //         System.out.println("winning");
    //         timeToPlay *= 0.8;
    //     }else if(evaluation * mySign <= -110){
    //         System.out.println("losing");
    //         timeToPlay *= 1.3;
    //     }
    //     totalTimeLeft -= timeTakenThisTurn;
    //     double timeTaken = 300 - totalTimeLeft;
    //     double timeManagementConstant = (1/2.9)*Math.log(-(timeTaken/100.0)+3.1)+0.6;
    //     timeManagementConstant = Math.max(timeManagementConstant, 0.05);
    //     System.out.println("Time Management Multiplier: " + timeManagementConstant);
    //     timeToPlay *= timeManagementConstant;
    //     timeToPlay = Math.min(timeToPlay, 18);

    //     int finalDepth = 1;
    //     for (int depth = 1; depth <= DEPTH_LIMIT; depth++) {
    //         try {
    //             bestMoveFound = minimax(depth, depth, isOpponentBlack, board.getLastMove(),
    //                 generation, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    //             searchThisMoveFirst = true;
    //         } catch (TimeoutException e) {
    //             //System.out.println("Fix?: " + fix + ", Total Depth " + totalDepth);
    //             break; // fallback to last bestMove
    //         }
    //         finalDepth = depth;
    //         if(bestMoveFound.getScore() * mySign >= 90000){
    //             foundWin = true;
    //             if(!previouslyFoundWin){
    //                 try{
    //                     double timeLeftThisTurn = (18 - (System.nanoTime() - startTime) / 1000000000.0)*1000;
    //                     double timeToWait = Math.min(
    //                         Math.min(3000 + 2000 * finalDepth, Math.max((long)timeLeftThisTurn, 0)),
    //                         totalTimeLeft * 300);
    //                     if(bestMoveFound.getScore() * mySign > 0 && !beingForced){
    //                         Thread.sleep((long)timeToWait);
    //                     }
    //                 }catch(Exception e){}
    //             }
    //             break;
    //         }
    //         if(lastBestMove.getMoveLocation() == bestMoveFound.getMoveLocation()){
    //             timeToPlay *= 0.92;
    //         }
    //         lastBestMove.set(bestMoveFound.getMoveLocation(), -1);
    //     }
    //     previouslyFoundWin = foundWin;
    //     System.out.println("--------------");
    //     System.out.println("Move Played: " + bestMoveFound.getMoveLocation());
    //     System.out.println("Evaluation: " + bestMoveFound.getScore());
    //     System.out.println("Positions searched: " + nodes);
    //     System.out.println("Depth reached: " + (finalDepth));
    //     totalDepth += finalDepth;
    //     int myMove = bestMoveFound.getMoveLocation();
    //     // System.out.println("Nodes: " + nodes);
    //     // System.out.println("TTHits: " + TThits);
    //     //System.out.println(myMove);
    //     timeTakenThisTurn = ((System.nanoTime() - startTime)/1000000000.0) + 2;
    //     return myMove;
    // }

    // changes the global variable bestMoveFound
    // returns final depth reached
    public int iterativeSearch(int depthLimit, Board board, int threadIndex){
        startTime = System.nanoTime();  // set global variable startTime to track time usage
        int finalDepth = 0;
        for (int depth = 1; depth <= depthLimit; depth++) {
            try{
                bestMovesFound[threadIndex] = minimax(depth, depth, isOpponentBlack, board.getLastMove(),
                    generation, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, threadIndex);
                searchThisMoveFirst[threadIndex] = true;
            }catch (TimeoutException e) {
                break;
            }
            finalDepth = depth;
        }
        return finalDepth;
    }

    // also adds reach moves (gap of 1 if also a threat of some kind)
    private void orderMoves(ArrayList<Integer> moves, LocationList[] threatMapList, long hash, 
         int threadIndex, boolean isRoot){
        G3VirtualBoard myVirtualBoard = myVirtualBoards[threadIndex];
        LocationList fourThreats = threatMapList[G3Constants.FOUR_THREAT_INDEX];
        for(int fourThreatIndex = 0; fourThreatIndex < fourThreats.getSize(); fourThreatIndex++){
            int move = fourThreats.getLocation(fourThreatIndex);
            if((true || fourThreats.getLocationInstances(move) > 1) && !moves.contains(move)){
                moves.add(move);
            }
        }
        fourThreats = threatMapList[G3Constants.FOUR_THREAT_INDEX + 4];
        for(int fourThreatIndex = 0; fourThreatIndex < fourThreats.getSize(); fourThreatIndex++){
            int move = fourThreats.getLocation(fourThreatIndex);
            if((true || fourThreats.getLocationInstances(move) > 1) && !moves.contains(move)){
                moves.add(move);
            }
        }
    
        // lowest priority - three threats, regular four threats
        for(int threatMapIndex = G3Constants.FOUR_THREAT_INDEX;
        threatMapIndex >= G3Constants.FOUR_THREAT_INDEX; threatMapIndex--){
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                int move = moves.get(moveIndex);
                if(threatMapList[threatMapIndex].containsLocation(move) || 
                threatMapList[threatMapIndex + 4].containsLocation(move)){ 
                    moves.remove(moveIndex); // if move found in BLACK or WHITE threatMap, bring it to front
                    moves.add(0, move);
                }
            }
        }

        // next open four threats and five threats
        for(int threatMapIndex = G3Constants.OPEN_FOUR_THREAT_INDEX;
        threatMapIndex >= G3Constants.FIVE_THREAT_INDEX; threatMapIndex--){
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                int move = moves.get(moveIndex);
                if(threatMapList[threatMapIndex].containsLocation(move) || 
                threatMapList[threatMapIndex + 4].containsLocation(move)){ 
                    moves.remove(moveIndex); // if move found in BLACK or WHITE threatMap, bring it to front
                    moves.add(0, move);
                }
            }
        }
        // finally, highest priority, table hash move
        TTEntry entry = table.get(hash);
        if(table.get(hash) != null){
            int move = getBestMoveFromEntry(entry);
            if(myVirtualBoard.isMoveValid(move)){
                moves.remove(Integer.valueOf(move));
                moves.add(0, move);
            }
        }
        //Variation for Lazy SMP
        if(isRoot && threadIndex != 0){
            int mainMove = (threadIndex < moves.size())? moves.get(threadIndex): moves.get(0);
            moves.remove(Integer.valueOf(mainMove));
            Collections.shuffle(moves);
            moves.add(0, mainMove);
        }
    }

    public SuperMove minimax(int originalDepth, int depth, boolean isMaximizingPlayer, int movePlayed,
     int generation, double alpha, double beta, int threadIndex) throws TimeoutException{
        nodes++;
        if((nodes % 100) == 0 && System.nanoTime() - startTime >= timeToPlay * 1000000000){
            throw new TimeoutException();
        }
        // check transposition table first to see minimax can be skipped for this node
        long hash = Zobrist.computeHash(myVirtualBoards[threadIndex]);
        // long recomputedHash = Zobrist.computeHash(myVirtualBoard);
        // if(hash != recomputedHash) {
        //     System.out.println("HASH MISMATCH at start of minimax!");
        //     System.out.println("Maintained: " + hash);
        //     System.out.println("Recomputed: " + recomputedHash);
        // }
        TTEntry currentEntry = table.get(hash);
        if(currentEntry != null){
            int tableMove = getBestMoveFromEntry(currentEntry);
            if(currentEntry.getDepth() >= depth){
                TThits++;
                switch(currentEntry.getType()){
                    case TTEntry.EXACT:
                        if(originalDepth != depth)
                            return new SuperMove(tableMove, currentEntry.getEval());
                        break;
                    case TTEntry.LOWER_BOUND:
                        if(currentEntry.getEval() >= beta && originalDepth != depth) {
                            prunes++;
                            return new SuperMove(tableMove, currentEntry.getEval());
                        }
                        alpha = Math.max(alpha, currentEntry.getEval());
                        break;

                    case TTEntry.UPPER_BOUND:
                        if(currentEntry.getEval() <= alpha && originalDepth != depth) {
                            prunes++;
                            return new SuperMove(tableMove, currentEntry.getEval());
                        }
                        beta = Math.min(beta, currentEntry.getEval());
                        break;
                }
            }
        }

        int thisPositionEval = myVirtualBoards[threadIndex].updateEvaluation(movePlayed);
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

                table.add(hash, new TTEntry(thisPositionEval, depth, TTEntry.EXACT, -1, generation, hash));
                return new SuperMove(-1, thisPositionEval);
            }
        }

        ArrayList<Integer> moves = myVirtualBoards[threadIndex].updateCandidateMoves(movePlayed);
        LocationList[] threatMapList = myVirtualBoards[threadIndex].fetchThreatMapList();
        boolean testCondition;
        int plyFromRoot = originalDepth - depth;
        if(isMaximizingPlayer){
            SuperMove bestMove = new SuperMove(-1, Double.NEGATIVE_INFINITY);
            orderMoves(moves, threatMapList, hash, threadIndex, originalDepth == depth);
            if(searchThisMoveFirst[threadIndex] && depth == originalDepth){
                TTEntry entry = table.get(hash);
                testCondition = (entry == null || entry.getDepth() < originalDepth);
                if(testCondition){
                    moves.remove(Integer.valueOf(bestMovesFound[threadIndex].getMoveLocation()));
                    moves.add(0, bestMovesFound[threadIndex].getMoveLocation());
                }
            }
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoards[threadIndex].placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex),
                 generation, newAlpha, newBeta, threadIndex).getScore();
                myVirtualBoards[threadIndex].undoStone();
                myVirtualBoards[threadIndex].setCandidateMoves(moves);
                myVirtualBoards[threadIndex].updateEvaluation(moves.get(moveIndex));
                if(newestScore > bestMove.getScore()){
                    bestMove.set(moves.get(moveIndex), newestScore);
                    if(originalDepth == depth)
                        bestMovesFound[threadIndex] = bestMove;
                }
                if(bestMove.getScore() > newAlpha)
                    newAlpha = bestMove.getScore();
                if(newBeta <= newAlpha){ //fail high - found something too good
                    prunes++;
                    int move = bestMove.getMoveLocation();
                    if(false && !threatMapList[G3Constants.FIVE_THREAT_INDEX].containsLocation(move)
                    && !threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)){
                        killerMoves.addKillerMove(move, depth);
                    }
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
            orderMoves(moves, threatMapList, hash, threadIndex, originalDepth == depth);
            if(searchThisMoveFirst[threadIndex] && depth == originalDepth){
                TTEntry entry = table.get(hash);
                testCondition = (entry == null || entry.getDepth() < originalDepth);
                if(testCondition){
                    moves.remove(Integer.valueOf(bestMovesFound[threadIndex].getMoveLocation()));
                    moves.add(0, bestMovesFound[threadIndex].getMoveLocation());
                }
            }
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoards[threadIndex].placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex), 
                    generation, newAlpha, newBeta, threadIndex).getScore();
                myVirtualBoards[threadIndex].undoStone();
                myVirtualBoards[threadIndex].setCandidateMoves(moves);
                myVirtualBoards[threadIndex].updateEvaluation(moves.get(moveIndex));
                if(newestScore < bestMove.getScore()){
                    bestMove.set(moves.get(moveIndex), newestScore);
                    if(originalDepth == depth)
                        bestMovesFound[threadIndex] = bestMove;
                }
                if(bestMove.getScore() < newBeta)
                    newBeta = bestMove.getScore();
                if(newBeta <= newAlpha){    // fail high - move too good
                    prunes++;
                    int move = bestMove.getMoveLocation();
                    if(false && !threatMapList[G3Constants.FIVE_THREAT_INDEX].containsLocation(move)
                    && !threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)){
                        killerMoves.addKillerMove(move, depth);
                    }
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

    // private SuperMove quiescenceSearch(int originalDepth, int depth, 
    //     boolean isMaximizingPlayer, int movePlayed, double alpha, double beta) throws TimeoutException{
    //     nodes++;
    //     if((nodes % 1000) == 0 && System.nanoTime() - startTime >= timeToPlay * 1000000000){
    //         throw new TimeoutException();
    //     }
    //     // check transposition table first to see minimax can be skipped for this node
    //     long hash = Zobrist.computeHash(myVirtualBoard);
  
    //     TTEntry currentEntry = table.get(hash);
    //     if(currentEntry != null){
    //         int tableMove = currentEntry.getBestMove();
    //         if(currentEntry.getDepth() >= depth){
    //             TThits++;
    //             switch(currentEntry.getType()){
    //                 case TTEntry.EXACT:
    //                     return new SuperMove(currentEntry.getBestMove(), currentEntry.getEval());
    //                 case TTEntry.LOWER_BOUND:
    //                     if(currentEntry.getEval() >= beta) {
    //                         return new SuperMove(currentEntry.getBestMove(), currentEntry.getEval());
    //                     }
    //                     alpha = Math.max(alpha, currentEntry.getEval());
    //                     break;

    //                 case TTEntry.UPPER_BOUND:
    //                     if(currentEntry.getEval() <= alpha) {
    //                         return new SuperMove(currentEntry.getBestMove(), currentEntry.getEval());
    //                     }
    //                     beta = Math.min(beta, currentEntry.getEval());
    //                     break;
    //             }
    //         }
    //     }

    //     int thisPositionEval = myVirtualBoard.updateEvaluation(movePlayed);
    //     ArrayList<Integer> moves = getQuiescenceMoves(hash);
    //     //ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
    //     if(depth == 0 || Math.abs(thisPositionEval) >= (G3Constants.GAME_WILL_BE_OVER) || thisPositionEval == G3Constants.GAME_DRAWN){
    //         if(Math.abs(thisPositionEval) == G3Constants.GAME_OVER){
    //             thisPositionEval += (Math.signum(thisPositionEval) * (20 + depth * 3)); // prioritizes quick win
    //         }
    //         else if(Math.abs(thisPositionEval) == G3Constants.GAME_WILL_BE_OVER){
    //             thisPositionEval += (Math.signum(thisPositionEval) * depth * 1); // prioritizes quick win
    //         }
    //         else if(thisPositionEval >= G3Constants.GAME_DRAWN){
    //             thisPositionEval = 0;
    //         }

    //         if(depth == 0 && moves.size() != 0){    // forced to stop quiescence search early = penalty
    //             if(isOpponentBlack)
    //                 thisPositionEval -= UNCERTAINTY_PENALTY;
    //             else
    //                 thisPositionEval += UNCERTAINTY_PENALTY;
    //         }
    //         return new SuperMove(-1, thisPositionEval);
    //     }

    //     // if no more quiescent moves, stop the search
    //     if(moves.size() == 0){
    //         return new SuperMove(-1, thisPositionEval);
    //     }

    //     // Stand pat code
    //     // assumes currentEvaluation is a lower bound for what current player can achieve
    //     // if currentEvaluation is already too good for the opponent to allow, prune
    //     if(isMaximizingPlayer){
    //         if(thisPositionEval - SAFETY_MARGIN >= beta){
    //             return new SuperMove(-1, thisPositionEval);
    //         }
    //     }else{  // isMinimizingPlayer
    //         if(thisPositionEval + SAFETY_MARGIN <= alpha){
    //             return new SuperMove(-1, thisPositionEval);
    //         }
    //     }

    //     if(isMaximizingPlayer){
    //         SuperMove bestMove = new SuperMove(-1, Double.NEGATIVE_INFINITY);
    //         double newestScore;
    //         double newAlpha = alpha;
    //         double newBeta = beta;
    //         for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
    //             myVirtualBoard.placeStone(moves.get(moveIndex));
    //             newestScore = quiescenceSearch(originalDepth, depth - 1, !isMaximizingPlayer,
    //              moves.get(moveIndex), newAlpha, newBeta).getScore();
    //             myVirtualBoard.undoStone();
    //             //myVirtualBoard.setCandidateMoves(moves);
    //             myVirtualBoard.updateEvaluation(moves.get(moveIndex));
    //             if(newestScore > bestMove.getScore()){
    //                 bestMove.set(moves.get(moveIndex), newestScore);
    //             }
    //             if(bestMove.getScore() > newAlpha)
    //                 newAlpha = bestMove.getScore();
    //             if(newBeta <= newAlpha){ //fail high - found something too good
    //                 return bestMove;
    //             }
    //         }
    //         return bestMove;
    //     }

    //     else{   // Minimizing Player
    //         SuperMove bestMove = new SuperMove(-1, Double.POSITIVE_INFINITY);
    //         double newestScore;
    //         double newAlpha = alpha;
    //         double newBeta = beta;
    //         for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
    //             myVirtualBoard.placeStone(moves.get(moveIndex));
    //             newestScore = quiescenceSearch(originalDepth, depth - 1, !isMaximizingPlayer,
    //              moves.get(moveIndex), newAlpha, newBeta).getScore();
    //             myVirtualBoard.undoStone();
    //             //myVirtualBoard.setCandidateMoves(moves);
    //             myVirtualBoard.updateEvaluation(moves.get(moveIndex));
    //             if(newestScore < bestMove.getScore()){
    //                 bestMove.set(moves.get(moveIndex), newestScore);
    //             }
    //             if(bestMove.getScore() < newBeta)
    //                 newBeta = bestMove.getScore();
    //             if(newBeta <= newAlpha){    // fail high - move too good
    //                 return bestMove;
    //             }
    //         }
    //         return bestMove;
    //     }
    // }

    // public ArrayList<Integer> getQuiescenceMoves(long hash){
    //     ArrayList<Integer> qMoves = new ArrayList<Integer>();
    //     LocationList[] threatMapList = myVirtualBoard.fetchThreatMapList();
    //     // first add 5-threats
    //     for(int i = G3Constants.FIVE_THREAT_INDEX; i < 5; i += 4){
    //         for(int moveIndex = 0; moveIndex < threatMapList[i].getSize(); moveIndex++){
    //             qMoves.add(threatMapList[i].getLocation(moveIndex));
    //         }
    //     }
    //     // then add 4-threats
    //     for(int i = G3Constants.FOUR_THREAT_INDEX; i < 7; i += 4){
    //         for(int moveIndex = 0; moveIndex < threatMapList[i].getSize(); moveIndex++){
    //             qMoves.add(threatMapList[i].getLocation(moveIndex));
    //         }
    //     }
        // add double 3-threats
        // for(int i = G3Constants.THREE_THREAT_INDEX; i < 8; i += 4){
        //     for(int moveIndex = 0; moveIndex < threatMapList[i].getSize(); moveIndex++){
        //         int move = threatMapList[i].getLocation(moveIndex);
        //         if(threatMapList[i].getLocationInstances(move) > 1)
        //             qMoves.add(threatMapList[i].getLocation(moveIndex));
        //     }
        // }
        // order table move first if existent
    //     TTEntry entry = table.get(hash);
    //     if(table.get(hash) != null){
    //         int move = entry.getBestMove();
    //         if(myVirtualBoard.isMoveValid(move)){
    //             qMoves.remove(Integer.valueOf(move));
    //             qMoves.add(0, move);
    //         }
    //     }
    //     return qMoves;
    // }

    public int getBestMoveFromEntry(TTEntry entry){
        return (entry != null)? entry.getBestMove(): -1;
    }

    public void shuffleArray(ArrayList<Integer> arrayList){
        int currentIndex = arrayList.size();
        while(currentIndex != 0){
            int randomIndex = (int)(Math.random() * currentIndex);
            currentIndex--;

            int temp = arrayList.get(randomIndex);
            arrayList.set(randomIndex, arrayList.get(currentIndex));
            arrayList.set(currentIndex, temp);
        }
    }

    public int findModeIndex(int[] array){
        if(array.length == 1){
            return 0;
        }

        Arrays.sort(array);
        int candidate = 0;
        int maxOccurences = 0;
        int currentOccurences = 1;
        int index = 0;

        for(int i = 1; i < array.length; i++){
            if(array[i] == array[i - 1]){
                currentOccurences++;
            }else{
                if(currentOccurences > maxOccurences){
                    maxOccurences = currentOccurences;
                    candidate = array[i - 1];
                    index = i - 1;
                }
                currentOccurences = 1;
            }
        }
        if(currentOccurences > maxOccurences){
            maxOccurences = currentOccurences;
            candidate = array[array.length - 1];
            index = array.length - 1;
        }
        return index;
    }

    public boolean equals(Object o){
        if(o instanceof G3VirtualBoard){
            G3VirtualBoard other = (G3VirtualBoard) o;
            if(super.equals(other) && 
               this.myVirtualBoards[0].getMoveHistory().equals(other.getMoveHistory())){
                return true;
            }
        }
        return false;
    }

}
