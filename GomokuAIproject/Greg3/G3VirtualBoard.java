package GomokuAIproject.Greg3;

import java.util.ArrayDeque;
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

    public G3VirtualBoard(boolean isOpponentBlack, boolean testSegments){
        super(Board.getInstance(), isOpponentBlack);
        moveHistory = new ArrayList<Integer>();
        allLines = new LineGroup(this,
            new Line[]{
            new Line(this, G3Constants.row0, testSegments),
            new Line(this, G3Constants.row1, testSegments),
            new Line(this, G3Constants.row2, testSegments),
            new Line(this, G3Constants.row3, testSegments),
            new Line(this, G3Constants.row4, testSegments),
            new Line(this, G3Constants.row5, testSegments),
            new Line(this, G3Constants.row6, testSegments),
            new Line(this, G3Constants.row7, testSegments),
            new Line(this, G3Constants.row8, testSegments),
            new Line(this, G3Constants.row9, testSegments),
            new Line(this, G3Constants.row10, testSegments),
            new Line(this, G3Constants.row11, testSegments),
            new Line(this, G3Constants.row12, testSegments),
            new Line(this, G3Constants.column0, testSegments),
            new Line(this, G3Constants.column1, testSegments),
            new Line(this, G3Constants.column2, testSegments),
            new Line(this, G3Constants.column3, testSegments),
            new Line(this, G3Constants.column4, testSegments),
            new Line(this, G3Constants.column5, testSegments),
            new Line(this, G3Constants.column6, testSegments),
            new Line(this, G3Constants.column7, testSegments),
            new Line(this, G3Constants.column8, testSegments),
            new Line(this, G3Constants.column9, testSegments),
            new Line(this, G3Constants.column10, testSegments),
            new Line(this, G3Constants.column11, testSegments),
            new Line(this, G3Constants.column12, testSegments),
            new Line(this, G3Constants.forwardDiagonal0, testSegments),
            new Line(this, G3Constants.forwardDiagonal1, testSegments),
            new Line(this, G3Constants.forwardDiagonal2, testSegments),
            new Line(this, G3Constants.forwardDiagonal3, testSegments),
            new Line(this, G3Constants.forwardDiagonal4, testSegments),
            new Line(this, G3Constants.forwardDiagonal5, testSegments),
            new Line(this, G3Constants.forwardDiagonal6, testSegments),
            new Line(this, G3Constants.forwardDiagonal7, testSegments),
            new Line(this, G3Constants.forwardDiagonal8, testSegments),
            new Line(this, G3Constants.forwardDiagonal9, testSegments),
            new Line(this, G3Constants.forwardDiagonal10, testSegments),
            new Line(this, G3Constants.forwardDiagonal11, testSegments),
            new Line(this, G3Constants.forwardDiagonal12, testSegments),
            new Line(this, G3Constants.forwardDiagonal13, testSegments),
            new Line(this, G3Constants.forwardDiagonal14, testSegments),
            new Line(this, G3Constants.forwardDiagonal15, testSegments),
            new Line(this, G3Constants.forwardDiagonal16, testSegments),
            new Line(this, G3Constants.backwardDiagonal0, testSegments),
            new Line(this, G3Constants.backwardDiagonal1, testSegments),
            new Line(this, G3Constants.backwardDiagonal2, testSegments),
            new Line(this, G3Constants.backwardDiagonal3, testSegments),
            new Line(this, G3Constants.backwardDiagonal4, testSegments),
            new Line(this, G3Constants.backwardDiagonal5, testSegments),
            new Line(this, G3Constants.backwardDiagonal6, testSegments),
            new Line(this, G3Constants.backwardDiagonal7, testSegments),
            new Line(this, G3Constants.backwardDiagonal8, testSegments),
            new Line(this, G3Constants.backwardDiagonal9, testSegments),
            new Line(this, G3Constants.backwardDiagonal10, testSegments),
            new Line(this, G3Constants.backwardDiagonal11, testSegments),
            new Line(this, G3Constants.backwardDiagonal12, testSegments),
            new Line(this, G3Constants.backwardDiagonal13, testSegments),
            new Line(this, G3Constants.backwardDiagonal14, testSegments),
            new Line(this, G3Constants.backwardDiagonal15, testSegments),
            new Line(this, G3Constants.backwardDiagonal16, testSegments)
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

    public LocationList getCandidateMoves(){
        LocationList moves = new LocationList();

        // finds moves that are adjacent to stones already placed
        for(int i = 0; i < 169; i++){
            if(super.getCellValue(i) != BoardConstants.EMPTY){
                for(int offset: OffsetConstants.REAL_OFFSETS){
                    if(isMoveValid(i, offset) && !moves.containsLocation(i + offset))
                        moves.addLocation(i + offset);
                    // if(isMoveValid(i + offset, offset) && !moves.contains(i + 2 * offset))
                    //     moves.add(i + 2 * offset);
                }
            }
        }

        if(moves.getSize() == 0){
            moves.addLocation(84);  // if stones placed yet, add possible move at center
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
    public int getEvaluation(){ // returns evaluation in 1st element of 1st array, then returns other threat arrays for move ordering
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
        return evaluation;
    }

    // negative = good for black, positive = good for white
    public int updateEvaluation(int locationOfStone){ // returns evaluation in 1st element of 1st array, then returns other threat arrays for move ordering
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
        return evaluation;    //TODO: implement evaluation
    }

    // fetches from LineGroup allLines
    public LocationList[] fetchThreatMapList(){
        return allLines.getThreatMapList();
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

    public String debugPrint(boolean forBlack){   // for debugging
        String print;
        int threatMapIndex;
        if(forBlack){
            print = "Black Threats: ";
            threatMapIndex = 0;
        }else{
            print = "White Threats: ";
            threatMapIndex = 4;
        }
        //return Integer.toString(allLines.getThreatMap(threatMapIndex).getSize());
        LocationList tempMap = allLines.getThreatMap(threatMapIndex);
        for(int i = 0; i < tempMap.getSize(); i++){
            print += tempMap.getLocation(i) + ", ";
        }
        return print;
    }

}
