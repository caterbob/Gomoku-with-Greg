package GomokuAIproject.Greg3;

import java.util.ArrayList;
import java.util.Collections;

import GomokuAIproject.Board;
import GomokuAIproject.Engine;
import GomokuAIproject.EngineHelpers.SuperMove;

public class Greg3 implements Engine{

    private G3VirtualBoard myVirtualBoard;
    private boolean isOpponentBlack;
    private int depth;

    
    public Greg3(boolean isOpponentBlack, int depth){
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard = new G3VirtualBoard(isOpponentBlack);
        this.depth = depth;
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        // lastEvaluation = 0;
        // currentEvaluation = 0;
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard.setIsOpponentBlack(isOpponentBlack);
    }

    public boolean getIsOpponentBlack(){
        return isOpponentBlack;
    }

    public int playFromPosition(Board board){
        // if(!play){
        //     return -1;
        // }
        myVirtualBoard.sync();  // syncs to current board state and clears move history
        ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
        SuperMove bestMoveFound = minimax(depth, depth, isOpponentBlack, board.getLastMove(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);   //make sure two numbers are the same
        int myMove = bestMoveFound.getMoveLocation();
        return myMove;

    }

    // also adds reach moves (gap of 1 if also a threat of some kind)
    private void orderMoves(ArrayList<Integer> moves, LocationList[] threatMapList){
        for(int threatMapIndex = G3Constants.THREE_THREAT_INDEX;
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
    }

    public SuperMove minimax(int originalDepth, int depth, boolean isMaximizingPlayer, int movePlayed, double alpha, double beta){

        double thisPositionEval = myVirtualBoard.updateEvaluation(movePlayed);
        LocationList[] threatMapList = myVirtualBoard.fetchThreatMapList();
        if(depth == 0 || Math.abs(thisPositionEval) >= (G3Constants.GAME_WILL_BE_OVER) || thisPositionEval == G3Constants.GAME_DRAWN){
            if(originalDepth != depth){
                if(Math.abs(thisPositionEval) == G3Constants.GAME_OVER){
                    thisPositionEval += (Math.signum(thisPositionEval) * (20 + depth * 3)); // prioritizes quick win
                }
                else if(Math.abs(thisPositionEval) == G3Constants.GAME_WILL_BE_OVER){
                    thisPositionEval += (Math.signum(thisPositionEval) * depth * 1); // prioritizes quick win
                }
                else if(thisPositionEval >= G3Constants.GAME_DRAWN){
                    thisPositionEval = 0;
                }
                
                return new SuperMove(-1, thisPositionEval);
            }
        }

        if(isMaximizingPlayer){
            SuperMove bestMove = new SuperMove(-1, Double.NEGATIVE_INFINITY);
            ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
            orderMoves(moves, myVirtualBoard.fetchThreatMapList());
            double newestScore;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex), alpha, beta).getScore();
                myVirtualBoard.undoStone();
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore > bestMove.getScore()){
                    bestMove = new SuperMove(moves.get(moveIndex), newestScore);
                }
                if(bestMove.getScore() > alpha)
                    alpha = bestMove.getScore();
                if(beta <= alpha)
                    break;
            }
            //System.out.println("Depth " + depth + ": " + bestMove.getMoveLocation());
            return bestMove;
        }

        else{   // Minimizing Player
            SuperMove bestMove = new SuperMove(-1, Double.POSITIVE_INFINITY);
            ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
            orderMoves(moves, myVirtualBoard.fetchThreatMapList());
            double newestScore;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, moves.get(moveIndex), alpha, beta).getScore();
                myVirtualBoard.undoStone();
                myVirtualBoard.updateEvaluation(moves.get(moveIndex));
                if(newestScore < bestMove.getScore()){
                    bestMove = new SuperMove(moves.get(moveIndex), newestScore);
                }
                if(bestMove.getScore() < beta)
                    beta = bestMove.getScore();
                if(beta <= alpha)
                    break;
            }
            //System.out.println("Depth " + depth + ": " + bestMove.getMoveLocation());
            return bestMove;
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
