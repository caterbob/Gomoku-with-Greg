package GomokuAIproject.Greg0;

import java.util.ArrayList;

import GomokuAIproject.Board;
import GomokuAIproject.Engine;
import GomokuAIproject.EngineHelpers.VirtualBoard;
import GomokuAIproject.Constants.OffsetConstants;

public class Greg0 implements Engine{

    private G0VirtualBoard myVirtualBoard;
    private boolean isOpponentBlack;
    
    public Greg0(boolean isOpponentBlack){
        myVirtualBoard = new G0VirtualBoard(isOpponentBlack);
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        this.isOpponentBlack = isOpponentBlack;
    }

    public boolean getIsOpponentBlack(){
        return isOpponentBlack; 
    }

    public int playFromPosition(Board board){
        myVirtualBoard.sync();
        ArrayList<Integer> moves = myVirtualBoard.getCandidateMoves();
        int myMove = moves.get((int)(Math.random() * moves.size()));
        return myMove;
    }

}
