package GomokuAIproject.Greg2;

import java.util.ArrayList;
import java.util.Collections;

import GomokuAIproject.Board;
import GomokuAIproject.Constants.EvaluationConstants;
import GomokuAIproject.Engine;
import GomokuAIproject.EngineHelpers.SuperMove;

public class Greg2 implements Engine{

    private G2VirtualBoard myVirtualBoard;
    private boolean isOpponentBlack;
    private int depth;

    
    public Greg2(boolean isOpponentBlack, int depth){
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard = new G2VirtualBoard(isOpponentBlack);
        this.depth = depth;
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        this.isOpponentBlack = isOpponentBlack;
        myVirtualBoard.setIsOpponentBlack(isOpponentBlack);
    }

    public boolean getIsOpponentBlack(){
        return isOpponentBlack;
    }

    public int playFromPosition(Board board){
        myVirtualBoard.sync();  // syncs to current board state and clears move history
        ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
        SuperMove bestMoveFound = minimax(depth, depth, isOpponentBlack, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);   //make sure two numbers are the same
        int myMove = bestMoveFound.getMoveLocation();
        // lastEvaluation = currentEvaluation;
        // currentEvaluation = (int)bestMoveFound.getScore();
        // if(currentEvaluation < 3000 && lastEvaluation >= 9998 && isOpponentBlack){
        //     //play = false;
        //     //System.out.println("white thought it was winning but changed its mind");
        // }
        // else if(currentEvaluation > -3000 && lastEvaluation <= -9998 && !isOpponentBlack){
        //     //play = false;
        //     //System.out.println("black thought it was winning but changed its mind");
        // }

        //System.out.println("Eval: " + bestMoveFound.getScore());
        // System.out.println("My Move: " + myMove);
        return myMove;

    }

    private void orderMoves(ArrayList<Integer> moves, int[][] evaluationData){
        for(int threatListIndex = 1; threatListIndex < 8; threatListIndex += 2){
            int[] threatList = evaluationData[threatListIndex];
            for(int threatLocationIndex = 0; threatLocationIndex < evaluationData[threatListIndex + 1][0]; threatLocationIndex++){
                int threatLocation = threatList[threatLocationIndex];
                for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                    if(moves.get(moveIndex) == threatLocation){
                        moves.remove(moveIndex);
                        moves.add(0, threatLocation);
                        break;
                    }
                }
            }
        }
    }

    public SuperMove minimax(int originalDepth, int depth, boolean isMaximizingPlayer, double alpha, double beta){

        int[][] evaluationData = myVirtualBoard.getEvaluation();
        double thisPositionEval = evaluationData[0][0]; // element [0][0] = evaluation value
        if(depth == 0 || Math.abs(thisPositionEval) >= (EvaluationConstants.GAME_WILL_BE_OVER) || thisPositionEval == EvaluationConstants.GAME_DRAWN){
            if(originalDepth != depth){
                if(Math.abs(thisPositionEval) == EvaluationConstants.GAME_OVER){
                    thisPositionEval += (Math.signum(thisPositionEval) * (20 + depth * 3)); // prioritizes quick win
                }
                else if(Math.abs(thisPositionEval) == EvaluationConstants.GAME_WILL_BE_OVER){
                    thisPositionEval += (Math.signum(thisPositionEval) * depth); // prioritizes quick win
                }
                else if(thisPositionEval >= EvaluationConstants.GAME_DRAWN){
                    thisPositionEval = 0;
                }
                
                return new SuperMove(-1, thisPositionEval);
            }
        }

        if(isMaximizingPlayer){
            SuperMove bestMove = new SuperMove(-1, Double.NEGATIVE_INFINITY);
            ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
            orderMoves(moves, evaluationData);
            double newestScore;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, alpha, beta).getScore();
                myVirtualBoard.undoStone();
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
            orderMoves(moves, evaluationData);
            double newestScore;
            for(int moveIndex = 0; moveIndex < moves.size(); moveIndex++){
                myVirtualBoard.placeStone(moves.get(moveIndex));
                newestScore = minimax(originalDepth, depth - 1, !isMaximizingPlayer, alpha, beta).getScore();
                myVirtualBoard.undoStone();
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
        if(o instanceof G2VirtualBoard){
            G2VirtualBoard other = (G2VirtualBoard) o;
            if(super.equals(other) && 
               this.myVirtualBoard.getMoveHistory().equals(other.getMoveHistory())){
                return true;
            }
        }
        return false;
    }

}
