package GomokuAIproject.Greg0;

import java.util.ArrayList;

import GomokuAIproject.Board;
import GomokuAIproject.EngineHelpers.VirtualBoard;
import GomokuAIproject.Constants.BoardConstants;
import GomokuAIproject.Constants.OffsetConstants;

public class G0VirtualBoard extends VirtualBoard{

    public G0VirtualBoard(boolean isOpponentBlack){
        super(Board.getInstance(), isOpponentBlack);
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        super.isOpponentBlack = isOpponentBlack;
    }

    public ArrayList<Integer> getCandidateMoves(){
        ArrayList<Integer> moves = new ArrayList<Integer>();

        // finds moves that are adjacent to stones already placed
        for(int i = 0; i < 169; i++){
            if(super.getCellValue(i) != BoardConstants.EMPTY){
                for(int offset: OffsetConstants.REAL_OFFSETS){
                    if(isMoveValid(i, offset) && !moves.contains(i + offset))
                        moves.add(i + offset);
                }
            }
        }

        if(moves.size() == 0){
            moves.add(84);  // if stones placed yet, add possible move at center
        }
        return moves;
    }
}
