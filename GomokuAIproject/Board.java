package GomokuAIproject;

import java.util.ArrayList;

import GomokuAIproject.Constants.BoardConstants;
import GomokuAIproject.GUI.DisplayBoard;

public class Board {
    
    private static Board instance = null;
    private static int[] board;
    private static boolean isBlackTurn;
    private static ArrayList<Integer> moveHistory;

    private Board(){
        board = new int[169];
        isBlackTurn = true;
        moveHistory = new ArrayList<Integer>();
    }

    public static Board getInstance(){
        if(instance == null)
            instance = new Board();
        return instance;
    }

    public static void reset(){
        instance = new Board();
        board = new int[169];
        isBlackTurn = true;
        DisplayBoard.getInstance().updateDisplay();
    }

    public int getLastMove(){
        if(moveHistory.size() > 0)
            return moveHistory.get(moveHistory.size()-1);
        return -1;
    }

    // returns true if location is valid
    public boolean placeStone(int location){
        if(location >= 0 && location <= 168 && board[location] == 0){
            if(isBlackTurn)
                board[location] = 1;
            else
                board[location] = 2;
            isBlackTurn = !isBlackTurn;
            DisplayBoard.getInstance().updateDisplay();
            moveHistory.add(location);
            return true;
        }
        return false;
    }

    public int getCellValue(int location){
        return board[location];
    }

    public boolean isBlackTurn(){
        return isBlackTurn;
    }

    public boolean isGameOver(){
        if(getGameState() == BoardConstants.ONGOING)
            return false;
        return true;
    }

    // see game states in BoardConstants
    public int getGameState(){
        // check for a horizontal win
        for(int rowStart = 0; rowStart <= 156; rowStart += 13){
            for(int i = rowStart; i < rowStart + 9; i++){
                if(board[i] == BoardConstants.EMPTY)
                    continue;
                if(board[i] == board[i + 1]
                    && board[i + 1] == board[i + 2]
                    && board[i + 2] == board[i + 3]
                    && board[i + 3] == board[i + 4]
                ){
                    if(board[i] == BoardConstants.BLACK){
                        return BoardConstants.BLACK_WINS;
                    }else{
                        return BoardConstants.WHITE_WINS;
                    }
                }
            }
        }
        // check for a vertical win
        for(int columnStart = 0; columnStart <= 12; columnStart++){
            for(int i = columnStart; i <= 116; i += 13){
                if(board[i] == BoardConstants.EMPTY)
                    continue;
                if(board[i] == board[i + 13]
                    && board[i + 13] == board[i + 26]
                    && board[i + 26] == board[i + 39]
                    && board[i + 39] == board[i + 52]
                ){
                    if(board[i] == BoardConstants.BLACK){
                        return BoardConstants.BLACK_WINS;
                    }else{
                        return BoardConstants.WHITE_WINS;
                    }
                }
            }
        }
        // check for forward-slash win (/)
        for(int nthDiagonal = 1; nthDiagonal <= 9; nthDiagonal++){
            int forwardDiagonalStart = 39 + 13 * nthDiagonal;
            for(int i = 0; i < nthDiagonal; i++){
                if(board[forwardDiagonalStart] == BoardConstants.EMPTY){
                    forwardDiagonalStart -= 12;
                    continue;
                }
                if(board[forwardDiagonalStart] == board[forwardDiagonalStart - 12]
                    && board[forwardDiagonalStart - 12] == board[forwardDiagonalStart - 24]
                    && board[forwardDiagonalStart - 24] == board[forwardDiagonalStart - 36]
                    && board[forwardDiagonalStart - 36] == board[forwardDiagonalStart - 48]
                ){
                    if(board[forwardDiagonalStart] == BoardConstants.BLACK){
                        return BoardConstants.BLACK_WINS;
                    }else{
                        return BoardConstants.WHITE_WINS;
                    }
                }
                forwardDiagonalStart -= 12;
            }
        }
        for(int nthDiagonal = 8; nthDiagonal >= 1; nthDiagonal--){
            int forwardDiagonalStart = 156 + (9 - nthDiagonal);
            for(int i = 0; i < nthDiagonal; i++){
                if(board[forwardDiagonalStart] == BoardConstants.EMPTY){
                    forwardDiagonalStart -= 12;
                    continue;
                }
                if(board[forwardDiagonalStart] == board[forwardDiagonalStart - 12]
                    && board[forwardDiagonalStart - 12] == board[forwardDiagonalStart - 24]
                    && board[forwardDiagonalStart - 24] == board[forwardDiagonalStart - 36]
                    && board[forwardDiagonalStart - 36] == board[forwardDiagonalStart - 48]
                ){
                    if(board[forwardDiagonalStart] == BoardConstants.BLACK){
                        return BoardConstants.BLACK_WINS;
                    }else{
                        return BoardConstants.WHITE_WINS;
                    }
                }
                forwardDiagonalStart -= 12;
            }
        }
        // check for backward-slash wins (\)
        for(int nthDiagonal = 1; nthDiagonal <= 9; nthDiagonal++){
            int backwardDiagonalStart = 51 + 13 * nthDiagonal;
            for(int i = 0; i < nthDiagonal; i++){
                if(board[backwardDiagonalStart] == BoardConstants.EMPTY){
                    backwardDiagonalStart -= 14;
                    continue;
                }
                if(board[backwardDiagonalStart] == board[backwardDiagonalStart - 14]
                    && board[backwardDiagonalStart - 14] == board[backwardDiagonalStart - 28]
                    && board[backwardDiagonalStart - 28] == board[backwardDiagonalStart - 42]
                    && board[backwardDiagonalStart - 42] == board[backwardDiagonalStart - 56]
                ){
                    if(board[backwardDiagonalStart] == BoardConstants.BLACK){
                        return BoardConstants.BLACK_WINS;
                    }else{
                        return BoardConstants.WHITE_WINS;
                    }
                }
                backwardDiagonalStart -= 14;
            }
        }
        for(int nthDiagonal = 8; nthDiagonal >= 1; nthDiagonal--){
            int backwardDiagonalStart = 168 - (9 - nthDiagonal);
            for(int i = 0; i < nthDiagonal; i++){
                if(board[backwardDiagonalStart] == BoardConstants.EMPTY){
                    backwardDiagonalStart -= 14;
                    continue;
                }
                if(board[backwardDiagonalStart] == board[backwardDiagonalStart - 14]
                    && board[backwardDiagonalStart - 14] == board[backwardDiagonalStart - 28]
                    && board[backwardDiagonalStart - 28] == board[backwardDiagonalStart - 42]
                    && board[backwardDiagonalStart - 42] == board[backwardDiagonalStart - 56]
                ){
                    if(board[backwardDiagonalStart] == BoardConstants.BLACK){
                        return BoardConstants.BLACK_WINS;
                    }else{
                        return BoardConstants.WHITE_WINS;
                    }
                }
                backwardDiagonalStart -= 14;
            }
        }

        // check for ties
        for(int i = 0; i < 169; i++){
            if(board[i] == BoardConstants.EMPTY)
                return BoardConstants.ONGOING;
        }
        //else
        return BoardConstants.TIE;
    }

    // for use on this site: https://www.gomocalc.com/#/
    public static String exportGame(){
        String export = "";
        String files = "abcdefghijklm";
        for(Integer move: moveHistory){
            String coordinate = "";
            coordinate += files.substring(move % 13, move % 13 + 1);
            coordinate += (14 - (move / 13 + 1));
            export += coordinate;
        }
        return export;
    }
}
