package GomokuAIproject.Greg1;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import GomokuAIproject.Board;
import GomokuAIproject.Engine;
import GomokuAIproject.EngineHelpers.SuperMove;
import GomokuAIproject.EngineHelpers.VirtualBoard;
import GomokuAIproject.Constants.EvaluationConstants;
import GomokuAIproject.Constants.OffsetConstants;

public class Greg1 implements Engine{

    private G1VirtualBoard myVirtualBoard;
    private boolean isOpponentBlack;
    private int depthSearched;
    
    public Greg1(boolean isOpponentBlack, int depthSearched){
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard = new G1VirtualBoard(isOpponentBlack);
        this.depthSearched = depthSearched;
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard.setIsOpponentBlack(isOpponentBlack);
    }

    public boolean getIsOpponentBlack(){
        return isOpponentBlack;
    }

    public int playFromPosition(Board board){
        myVirtualBoard.sync();
        myVirtualBoard.clearMoveHistory(); // clear move history for new search
        ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
        Instant startTime = Instant.now();
        SuperMove bestMoveFound = minimax(depthSearched, isOpponentBlack);
        Instant endTime = Instant.now();
        double duration = Duration.between(startTime, endTime).toMillis()/1000.0;
        int myMove = bestMoveFound.getMoveLocation();
        //System.out.println("Evaluation: " + bestMoveFound.getScore() + " in " + duration + " seconds.");
        return myMove;
    }

    public SuperMove minimax(int depth, boolean isMaximizingPlayer){

        double thisPositionEval = myVirtualBoard.getEvaluation();
        if(depth == 0 || Math.abs(thisPositionEval) == EvaluationConstants.GAME_OVER){
            if(depth != 0){
                thisPositionEval += (Math.signum(thisPositionEval) * depth); // prioritizes quick win
            }
            return new SuperMove(-1, thisPositionEval);
        }

        if(isMaximizingPlayer){
            SuperMove bestMove = new SuperMove(-1, Double.NEGATIVE_INFINITY);
            ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
            double newestScore;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(depth - 1, !isMaximizingPlayer).getScore();
                myVirtualBoard.undoStone();
                if(newestScore > bestMove.getScore()){
                    bestMove = new SuperMove(moves.get(moveIndex), newestScore);
                }
            }
            return bestMove;
        }

        else{   // Minimizing Player
            SuperMove bestMove = new SuperMove(-1, Double.POSITIVE_INFINITY);
            ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
            double newestScore;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(depth - 1, !isMaximizingPlayer).getScore();
                myVirtualBoard.undoStone();
                if(newestScore < bestMove.getScore()){
                    bestMove = new SuperMove(moves.get(moveIndex), newestScore);
                }
            }
            return bestMove;
        }

    }

    public boolean equals(Object o){
        if(o instanceof G1VirtualBoard){
            G1VirtualBoard other = (G1VirtualBoard) o;
            if(super.equals(other) && 
               this.myVirtualBoard.getMoveHistory().equals(other.getMoveHistory())){
                return true;
            }
        }
        return false;
    }

}
