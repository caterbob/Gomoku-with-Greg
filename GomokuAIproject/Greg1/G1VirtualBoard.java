package GomokuAIproject.Greg1;

import java.util.ArrayList;

import GomokuAIproject.Board;
import GomokuAIproject.EngineHelpers.VirtualBoard;
import GomokuAIproject.Constants.BoardConstants;
import GomokuAIproject.Constants.OffsetConstants;

public class G1VirtualBoard extends VirtualBoard{

    private ArrayList<Integer> moveHistory;

    public G1VirtualBoard(boolean isOpponentBlack){
        super(Board.getInstance(), isOpponentBlack);
        moveHistory = new ArrayList<Integer>();
    }

    public G1VirtualBoard(G1VirtualBoard toCopy){
        super(toCopy);
        this.moveHistory = toCopy.getMoveHistory();
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        super.isOpponentBlack = isOpponentBlack;
    }

    public ArrayList<Integer> getMoveHistory(){
        return moveHistory;
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

    // negative = good for black, positive = good for white
    public double getEvaluation(){
        double evaluation = 0;
        // horizontal scanning
        for(int rowStart = 0; rowStart <= 156; rowStart += 13){
            int currentStreak = 0;
            for(int i = rowStart + 1; i < rowStart + 13; i++){
                if(currentStreak >= 4){
                    if(board[i - 1] == BoardConstants.BLACK)
                        return -9999;
                    return 9999;
                }
                if(board[i - 1] == board[i] && board[i] != BoardConstants.EMPTY){
                    currentStreak++;
                    evaluation += (board[i]*2 - 3);
                }
                else{
                    currentStreak = 0;
                }
            }
        }
        // check for a vertical win
        for(int columnStart = 13; columnStart <= 25; columnStart++){
            int currentStreak = 0;
            for(int i = columnStart; i <= 168; i += 13){
                if(currentStreak >= 4){
                    if(board[i - 13] == BoardConstants.BLACK)
                        return -9999;
                    return 9999;
                }
                if(board[i - 13] == board[i] && board[i] != BoardConstants.EMPTY){
                    currentStreak++;
                    evaluation += (board[i]*2 - 3);
                }
                else{
                    currentStreak = 0;
                }
            }
        }
        // check for forward-slash win (/)
        for(int nthDiagonal = 1; nthDiagonal <= 9; nthDiagonal++){
            int currentStreak = 0;
            int forwardDiagonalStart = 27 + 13 * nthDiagonal;
            for(int i = 0; i < nthDiagonal + 3; i++){
                if(currentStreak >= 4){
                    if(board[forwardDiagonalStart + 12] == BoardConstants.BLACK)
                        return -9999;
                    return 9999;
                }
                if(board[forwardDiagonalStart + 12] == board[forwardDiagonalStart] && board[forwardDiagonalStart] != BoardConstants.EMPTY){
                    currentStreak++;
                    evaluation += (board[forwardDiagonalStart]*2 - 3);
                }
                else{
                    currentStreak = 0;
                }
                forwardDiagonalStart -= 12;
            }
        }
        for(int nthDiagonal = 8; nthDiagonal >= 1; nthDiagonal--){
            int currentStreak = 0;
            int forwardDiagonalStart = 144 + (9 - nthDiagonal);
            for(int i = 0; i < nthDiagonal + 3; i++){
                if(currentStreak >= 4){
                    if(board[forwardDiagonalStart + 12] == BoardConstants.BLACK)
                        return -9999;
                    return 9999;
                }
                if(board[forwardDiagonalStart + 12] == board[forwardDiagonalStart] && board[forwardDiagonalStart] != BoardConstants.EMPTY){
                    currentStreak++;
                    evaluation += (board[forwardDiagonalStart]*2 - 3);
                }
                else{
                    currentStreak = 0;
                }
                forwardDiagonalStart -= 12;
            }
        }
        // check for backward-slash wins (\)
        for(int nthDiagonal = 1; nthDiagonal <= 9; nthDiagonal++){
            int currentStreak = 0;
            int backwardDiagonalStart = 37 + 13 * nthDiagonal;
            for(int i = 0; i < nthDiagonal + 3; i++){
                if(currentStreak >= 4){
                    if(board[backwardDiagonalStart + 14] == BoardConstants.BLACK)
                        return -9999;
                    return 9999;
                }
                if(board[backwardDiagonalStart + 14] == board[backwardDiagonalStart] && board[backwardDiagonalStart] != BoardConstants.EMPTY){
                    currentStreak++;
                    evaluation += (board[backwardDiagonalStart]*2 - 3);
                }
                else{
                    currentStreak = 0;
                }
                backwardDiagonalStart -= 14;
            }
        }
        for(int nthDiagonal = 8; nthDiagonal >= 1; nthDiagonal--){
            int currentStreak = 0;
            int backwardDiagonalStart = 154 - (9 - nthDiagonal);
            for(int i = 0; i < nthDiagonal + 3; i++){
                if(currentStreak >= 4){
                    if(board[backwardDiagonalStart + 14] == BoardConstants.BLACK)
                        return -9999;
                    return 9999;
                }
                if(board[backwardDiagonalStart + 14] == board[backwardDiagonalStart] && board[backwardDiagonalStart] != BoardConstants.EMPTY){
                    currentStreak++;
                    evaluation += (board[backwardDiagonalStart]*2 - 3);
                }
                else{
                    currentStreak = 0;
                }
                backwardDiagonalStart -= 14;
            }
        }
        return evaluation;
    }

}
