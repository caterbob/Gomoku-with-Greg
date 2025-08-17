package GomokuAIproject.Greg3;

import java.util.ArrayList;
import java.util.Arrays;

import GomokuAIproject.Board;
import GomokuAIproject.EngineHelpers.VirtualBoard;
import GomokuAIproject.Constants.BoardConstants;
import GomokuAIproject.Constants.EvaluationConstants;
import GomokuAIproject.Constants.OffsetConstants;

public class G3VirtualBoard extends VirtualBoard{

    private ArrayList<Integer> moveHistory;
    private LineGroup allLines;

    public G3VirtualBoard(boolean isOpponentBlack){
        super(Board.getInstance(), isOpponentBlack);
        moveHistory = new ArrayList<Integer>();
        allLines = new LineGroup(this,
            new Line[]{
            new Line(this, G3Constants.row0),
            new Line(this, G3Constants.row1),
            new Line(this, G3Constants.row2),
            new Line(this, G3Constants.row3),
            new Line(this, G3Constants.row4),
            new Line(this, G3Constants.row5),
            new Line(this, G3Constants.row6),
            new Line(this, G3Constants.row7),
            new Line(this, G3Constants.row8),
            new Line(this, G3Constants.row9),
            new Line(this, G3Constants.row10),
            new Line(this, G3Constants.row11),
            new Line(this, G3Constants.row12),
            new Line(this, G3Constants.column0),
            new Line(this, G3Constants.column1),
            new Line(this, G3Constants.column2),
            new Line(this, G3Constants.column3),
            new Line(this, G3Constants.column4),
            new Line(this, G3Constants.column5),
            new Line(this, G3Constants.column6),
            new Line(this, G3Constants.column7),
            new Line(this, G3Constants.column8),
            new Line(this, G3Constants.column9),
            new Line(this, G3Constants.column10),
            new Line(this, G3Constants.column11),
            new Line(this, G3Constants.column12),
            new Line(this, G3Constants.forwardDiagonal0),
            new Line(this, G3Constants.forwardDiagonal1),
            new Line(this, G3Constants.forwardDiagonal2),
            new Line(this, G3Constants.forwardDiagonal3),
            new Line(this, G3Constants.forwardDiagonal4),
            new Line(this, G3Constants.forwardDiagonal5),
            new Line(this, G3Constants.forwardDiagonal6),
            new Line(this, G3Constants.forwardDiagonal7),
            new Line(this, G3Constants.forwardDiagonal8),
            new Line(this, G3Constants.forwardDiagonal9),
            new Line(this, G3Constants.forwardDiagonal10),
            new Line(this, G3Constants.forwardDiagonal11),
            new Line(this, G3Constants.forwardDiagonal12),
            new Line(this, G3Constants.forwardDiagonal13),
            new Line(this, G3Constants.forwardDiagonal14),
            new Line(this, G3Constants.forwardDiagonal15),
            new Line(this, G3Constants.forwardDiagonal16),
            new Line(this, G3Constants.backwardDiagonal0),
            new Line(this, G3Constants.backwardDiagonal1),
            new Line(this, G3Constants.backwardDiagonal2),
            new Line(this, G3Constants.backwardDiagonal3),
            new Line(this, G3Constants.backwardDiagonal4),
            new Line(this, G3Constants.backwardDiagonal5),
            new Line(this, G3Constants.backwardDiagonal6),
            new Line(this, G3Constants.backwardDiagonal7),
            new Line(this, G3Constants.backwardDiagonal8),
            new Line(this, G3Constants.backwardDiagonal9),
            new Line(this, G3Constants.backwardDiagonal10),
            new Line(this, G3Constants.backwardDiagonal11),
            new Line(this, G3Constants.backwardDiagonal12),
            new Line(this, G3Constants.backwardDiagonal13),
            new Line(this, G3Constants.backwardDiagonal14),
            new Line(this, G3Constants.backwardDiagonal15),
            new Line(this, G3Constants.backwardDiagonal16)
        });
        allLines.getEvaluation();
    }

    public G3VirtualBoard(G3VirtualBoard toCopy){
        super(toCopy);
        this.moveHistory = toCopy.getMoveHistory();
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        super.isOpponentBlack = isOpponentBlack;
    }

    public ArrayList<Integer> getMoveHistory(){
        return moveHistory;
    }

    // overridden to handle padding and use of index 169
    public int getCellValue(int location){
        if(location == 169){
            return 3;
        }
        return board[location];
    }

    public void sync(){
        super.sync();
        clearMoveHistory();
        getEvaluation();
    }

    public ArrayList<Integer> getCandidateMoves(){
        ArrayList<Integer> moves = new ArrayList<Integer>();

        // finds moves that are adjacent to stones already placed
        for(int i = 0; i < 169; i++){
            if(super.getCellValue(i) != BoardConstants.EMPTY){
                for(int offset: OffsetConstants.REAL_OFFSETS){
                    if(isMoveValid(i, offset) && !moves.contains(i + offset))
                        moves.add(i + offset);
                    // if(isMoveValid(i + offset, offset) && !moves.contains(i + 2 * offset))
                    //     moves.add(i + 2 * offset);
                }
            }
        }

        if(moves.size() == 0){
            moves.add(84);  // if stones placed yet, add possible move at center
        }
        return moves;
    }

    public boolean placeStone(int location){
        if(location >= 0 && location <= 168 && board[location] == 0){
            if(isBlackTurn)
                board[location] = 1;
            else
                board[location] = 2;
            moveHistory.add(location);
            isBlackTurn = !isBlackTurn;
            return true;
        }
        return false;
    }

    public void undoStone(){
        board[moveHistory.get(moveHistory.size() - 1)] = 0;
        moveHistory.remove(moveHistory.size() - 1);
        isBlackTurn = !isBlackTurn;
    }

    public void clearMoveHistory(){
        moveHistory.clear();
    }

    // used when initializing virtualBoard
    public int[][] getEvaluation(){ // returns evaluation in 1st element of 1st array, then returns other threat arrays for move ordering
        int evaluation = allLines.getEvaluation();
        boolean emptySpaceFound = false;
        for(int i = 0; i < 169; i++){
            if(getCellValue(i) == BoardConstants.EMPTY){
                emptySpaceFound = true;
                break;
            }
        }
        if(!emptySpaceFound)
            evaluation = G3Constants.GAME_DRAWN;
        return new int[][]{{evaluation}};
    }

    // negative = good for black, positive = good for white
    public int[][] updateEvaluation(int locationOfStone){ // returns evaluation in 1st element of 1st array, then returns other threat arrays for move ordering
        int evaluation = allLines.updateEvaluation(locationOfStone);
        boolean emptySpaceFound = false;
        for(int i = 0; i < 169; i++){
            if(getCellValue(i) == BoardConstants.EMPTY){
                emptySpaceFound = true;
                break;
            }
        }
        if(!emptySpaceFound)
            evaluation = G3Constants.GAME_DRAWN;
        return new int[][]{{evaluation}};    //TODO: implement evaluation
    }

    private boolean isDraw(){
        boolean moveExists = false;
        for(int i = 0; i < 169; i++){
            if(board[i] == BoardConstants.EMPTY)
                moveExists = true;
        }
        if(moveExists)
            return false;
        return true;
    }

}
