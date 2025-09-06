package GomokuAIproject.Greg3;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
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
    private int totalDepth;

    // stats
    private long startTime;
    private int TThits;
    private int nodes;
    private int prunes;
    private double movesPlayed;

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
        myVirtualBoards = new G3VirtualBoard[8];
        for(int i = 0; i < 8; i++){
            myVirtualBoards[i] = new G3VirtualBoard(isOpponentBlack, testSegments, fix);
        }
        // if(fix){
        //     table = new TranspositionTable(1000000);
        // }else{
        //     table = new TranspositionTable(G3Constants.TTSize);
        // }
        table = new TranspositionTable(G3Constants.TTSize, fix);
        table.initializeTable();
        killerMoves = new KillerMoveTable(DEPTH_LIMIT, 3);
        this.timeToPlay = timeToPlay;
        generation = 0;
        nodes = 0;
        totalDepth = 0;
        movesPlayed = 0;
        this.fix = fix;
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        // lastEvaluation = 0;
        // currentEvaluation = 0;
        this.isOpponentBlack = isOpponentBlack;
        for(int i = 0; i < 8; i++){
            myVirtualBoards[i].setIsOpponentBlack(isOpponentBlack);
        }
        table.clear();
        killerMoves.clear();
        generation = 0;
        //System.out.println("Fix? " + fix + ", Average Depth: " + totalDepth / movesPlayed);
        System.out.println("Average Nodes Per Second: " + (int)((nodes / movesPlayed) / timeToPlay) + " N/s");
    }

    public boolean getIsOpponentBlack(){
        return isOpponentBlack;
    }

    public int playFromPosition(Board board){
        // if(!play){
        //     return -1;
        // }
        generation++;
        movesPlayed++;
        // syncs to current board state and clears move history
        for(int i = 0; i < 8; i++){
            myVirtualBoards[i].sync();
            myVirtualBoards[i].getEvaluation();
        }
        //table.clear();
        TThits = 0;
        //nodes = 0;
        prunes = 0;
        ArrayList<Integer> moves = myVirtualBoards[0].getCandidateMoves();
        bestMoveFound = new SuperMove(moves.get(0), 0);
        searchThisMoveFirst = false;
        // bring old killer moves one ply closer to root for new iteration
        killerMoves.shiftBackward();
        startTime = System.nanoTime();
        int finalDepth = 1;
        for (int depth = 1; depth <= DEPTH_LIMIT; depth++) {
            try {
                bestMoveFound = minimax(depth, depth, isOpponentBlack, board.getLastMove(),
                    generation, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, myVirtualBoards[0]);
                searchThisMoveFirst = true;
            } catch (TimeoutException e) {
                break; // fallback to last bestMove
            }
            
            finalDepth = depth;
            if(depth == DEPTH_LIMIT){
                //System.out.println("Fix?: " + fix + ", Total Depth " + totalDepth);
            }
        }
        //System.out.println("Depth reached (partial): " + (finalDepth));
        //System.out.println("Evaluation: " + bestMoveFound.getScore());
        //System.out.println("Positions searched: " + nodes);
        //System.out.println("Fix?: " + fix + ", Total Depth " + totalDepth);
        totalDepth += finalDepth;
        int myMove = bestMoveFound.getMoveLocation();
        // System.out.println("Nodes: " + nodes);
        // System.out.println("TTHits: " + TThits);
        //System.out.println(myMove);
        return myMove;

    }

    private SuperMove search(Board board, ArrayList<Integer> moves){
        int[] movesPerThread;
        Thread[] threads;

        orderMoves(moves, myVirtualBoards[0].fetchThreatMapList(), 
        Zobrist.computeHash(myVirtualBoards[0]),0, myVirtualBoards[0]);

        if(moves.size() > 8){
            int baseLoad = moves.size() / 8;
            int remainder = moves.size() % 8;
            movesPerThread = new int[8];
            int threadsAssigned = 0;
            for(int i = 0; i < 8; i++){
                movesPerThread[7 - i] = baseLoad + ((i < remainder)? 0: 1);
            }
        }else if(moves.size() > 1){
            int upperLoad = Math.ceilDiv(moves.size(), 2);
            int lowerLoad = Math.floorDiv(moves.size(), 2);
            movesPerThread = new int[]{lowerLoad, upperLoad};
        }else{
            movesPerThread = new int[]{1};
        }
        
        int[] threadBestMoves = new int[movesPerThread.length];
        startTime = System.nanoTime();
        for (int depth = 1; depth <= DEPTH_LIMIT; depth++) {
            // create Threads
            threads = new Thread[movesPerThread.length];
            for(int i = movesPerThread.length - 1; i >= 0; i--){
                int start = moves.size() - 1;
                for(int j = movesPerThread.length - 1; j > i; j--){
                    start += movesPerThread[j];
                }
                int end = start + movesPerThread[i];
                final int finalStart = start;
                final int finalEnd = end;
                final int threadIndex = i;
                threads[threadIndex] = new Thread(() -> {
                    if(isOpponentBlack)
                    for(int moveIndex = finalStart; moveIndex > finalEnd; moveIndex++){
                        myVirtualBoards[threadIndex].placeStone(moves.get(moveIndex));
                        try{
                            threadBestMoves[threadIndex] = minimax(depth, depth, isOpponentBlack, 
                            board.getLastMove(), generation, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                            myVirtualBoards[threadIndex]).getMoveLocation();
                        }catch(TimeoutException e){

                        }
                    }
                });
            }
            try {
                searchThisMoveFirst = true;
            } catch (TimeoutException e) {
                break; // fallback to last bestMove
            }
        }
        return bestMoveFound;
    }

    // also adds reach moves (gap of 1 if also a threat of some kind)
    private void orderMoves(ArrayList<Integer> moves, LocationList[] threatMapList, 
        long hash, int plyFromRoot, G3VirtualBoard myVirtualBoard){
        if(fix){
            LocationList usedList;
            usedList = threatMapList[G3Constants.FOUR_THREAT_INDEX];
            for(int i = 0; i < usedList.getSize(); i++){
                int threat = usedList.getLocation(i);
                if(!moves.contains(threat) && usedList.getLocationInstances(threat) > 1)
                    moves.add(threat); 
            }
            usedList = threatMapList[G3Constants.FOUR_THREAT_INDEX + 4];
            for(int i = 0; i < usedList.getSize(); i++){
                int threat = usedList.getLocation(i);
                if(!moves.contains(threat) && usedList.getLocationInstances(threat) > 1)
                    moves.add(threat);
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
        // next killer moves
        // int[] killerMoveList = killerMoves.getKillers(plyFromRoot);
        // for(int move: killerMoveList){
        //     if(moves.contains(move)){
        //         moves.remove(Integer.valueOf(move));
        //         moves.add(0, move);
        //     }
        // }
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
            int move = entry.getBestMove();
            if(myVirtualBoard.isMoveValid(move)){
                moves.remove(Integer.valueOf(move));
                moves.add(0, move);
            }
        }
    }

    public SuperMove minimax(int originalDepth, int depth, boolean isMaximizingPlayer, int movePlayed,
     int generation, double alpha, double beta, G3VirtualBoard myVirtualBoard) throws TimeoutException{
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
                }else if(depth == 0 && false){
                    // thisPositionEval = (int)minimax(1, 1, isMaximizingPlayer, movePlayed, generation,
                    //     alpha, beta, true, 0).getScore();
                }

                table.add(hash, thisPositionEval, depth, TTEntry.EXACT, -1, generation);
                return new SuperMove(-1, thisPositionEval);
            }
        }


        ArrayList<Integer> moves = myVirtualBoard.updateCandidateMoves(movePlayed);
        LocationList[] threatMapList = myVirtualBoard.fetchThreatMapList();
        int addTacticalMove = 0;
        int plyFromRoot = originalDepth - depth;
        // boolean fiveThreatsOnly = false;
        // if(fix && (threatMapList[G3Constants.FIVE_THREAT_INDEX].getSize() > 0
        //     || threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].getSize() > 0)){
        //         fiveThreatsOnly = true;
        // }
        if(isMaximizingPlayer){
            SuperMove bestMove = new SuperMove(-1, Double.NEGATIVE_INFINITY);
            orderMoves(moves, threatMapList, hash, plyFromRoot, myVirtualBoard);
            if(searchThisMoveFirst && depth == originalDepth){
                moves.remove(Integer.valueOf(bestMoveFound.getMoveLocation()));
                moves.add(0, bestMoveFound.getMoveLocation());
            }
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){

                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex),
                 generation, newAlpha, newBeta, myVirtualBoard).getScore();
                myVirtualBoard.undoStone();
                addTacticalMove = 0;
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
                    // if(fix && !threatMapList[G3Constants.FIVE_THREAT_INDEX].containsLocation(move)
                    // && !threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)){
                    //     killerMoves.addKillerMove(move, depth);
                    // }
                    table.add(hash, (int)bestMove.getScore(), depth, TTEntry.LOWER_BOUND, bestMove.getMoveLocation(), generation);
                    return bestMove;
                }
            }
            if(bestMove.getScore() <= alpha){   // fail low - didn't find something better
                table.add(hash, (int)bestMove.getScore(), depth, TTEntry.UPPER_BOUND, bestMove.getMoveLocation(), generation);
            }else{
                table.add(hash, (int)bestMove.getScore(), depth, TTEntry.EXACT, bestMove.getMoveLocation(), generation);
            }
            return bestMove;
        }

        else{   // Minimizing Player
            SuperMove bestMove = new SuperMove(-1, Double.POSITIVE_INFINITY);
            orderMoves(moves, threatMapList, hash, plyFromRoot, myVirtualBoard);
            if(searchThisMoveFirst && depth == originalDepth){
                moves.remove(Integer.valueOf(bestMoveFound.getMoveLocation()));
                moves.add(0, bestMoveFound.getMoveLocation());
            }
            double newestScore;
            double newAlpha = alpha;
            double newBeta = beta;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex), 
                    generation, newAlpha, newBeta, myVirtualBoard).getScore();
                myVirtualBoard.undoStone();
                addTacticalMove = 0;
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
                    // if(fix && !threatMapList[G3Constants.FIVE_THREAT_INDEX].containsLocation(move)
                    // && !threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)){
                    //     killerMoves.addKillerMove(move, depth);
                    // }
                    table.add(hash, (int)bestMove.getScore(), depth, TTEntry.UPPER_BOUND, bestMove.getMoveLocation(), generation);
                    return bestMove;
                }
            }
            //System.out.println("Depth " + depth + ": " + bestMove.getMoveLocation());
            if(bestMove.getScore() >= beta){   // fail low - didn't find something better
                table.add(hash, (int)bestMove.getScore(), depth, TTEntry.LOWER_BOUND, bestMove.getMoveLocation(), generation);
            }else{
                table.add(hash, (int)bestMove.getScore(), depth, TTEntry.EXACT, bestMove.getMoveLocation(), generation);
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
    //     // add double 3-threats
    //     // for(int i = G3Constants.THREE_THREAT_INDEX; i < 8; i += 4){
    //     //     for(int moveIndex = 0; moveIndex < threatMapList[i].getSize(); moveIndex++){
    //     //         int move = threatMapList[i].getLocation(moveIndex);
    //     //         if(threatMapList[i].getLocationInstances(move) > 1)
    //     //             qMoves.add(threatMapList[i].getLocation(moveIndex));
    //     //     }
    //     // }
    //     // order table move first if existent
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

    // if move is in a five threat or four threat space
    public boolean isTacticalMove(LocationList[] threatMapList, int move){
        return (
            threatMapList[G3Constants.FOUR_THREAT_INDEX].containsLocation(move)
            || threatMapList[G3Constants.FOUR_THREAT_INDEX + 4].containsLocation(move)
            || threatMapList[G3Constants.FIVE_THREAT_INDEX].containsLocation(move)
            || threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)
        );
    }

    // if move is five threat (or blocks one)
    public boolean isFiveThreat(LocationList[] threatMapList, int move){
        return (
            threatMapList[G3Constants.FIVE_THREAT_INDEX].containsLocation(move)
            || threatMapList[G3Constants.FIVE_THREAT_INDEX + 4].containsLocation(move)
        );
    }

}
