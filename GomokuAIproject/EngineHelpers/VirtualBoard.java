package GomokuAIproject.EngineHelpers;

import GomokuAIproject.Board;
import GomokuAIproject.Constants.OffsetConstants;

public abstract class VirtualBoard {
    
    protected int[] board;
    protected int[] metaBoard;  // contains padding of -1 to ensure valid moves
    protected boolean isBlackTurn;
    protected boolean isOpponentBlack;

    public VirtualBoard(Board originPosition, boolean isOpponentBlack){
        board = new int[169];
        for(int i = 0; i < 169; i++){
            board[i] = originPosition.getCellValue(i);
            this.isBlackTurn = originPosition.isBlackTurn();
        }
        this.isOpponentBlack = isOpponentBlack;
        metaBoard = new int[225];
        initializeMetaBoard();

    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        this.isOpponentBlack = isOpponentBlack;
    }

    public VirtualBoard(VirtualBoard toCopy){
        board = new int[169];
        for(int i = 0; i < 169; i++){
            board[i] = toCopy.getCellValue(i);
            this.isBlackTurn = toCopy.isBlackTurn();
            this.isOpponentBlack = toCopy.isOpponentBlack();
        }
        metaBoard = new int[225];
        initializeMetaBoard();
    }

    // syncs virtual board back with current board state
    public void sync(){
        for(int i = 0; i < 169; i++){
            board[i] = Board.getInstance().getCellValue(i);
        }
        this.isBlackTurn = Board.getInstance().isBlackTurn();
    }

    // returns true if location is valid
    public boolean placeStone(int location){
        if(location >= 0 && location <= 168 && board[location] == 0){
            if(isBlackTurn)
                board[location] = 1;
            else
                board[location] = 2;
            isBlackTurn = !isBlackTurn;
            return true;
        }
        return false;
    }

    public int getCellValue(int location){
        return board[location];
    }

    public int getMetaValue(int location){
        return metaBoard[location];
    }

    public boolean isBlackTurn(){
        return isBlackTurn;
    }

    public boolean isOpponentBlack(){
        return isOpponentBlack;
    }

    public boolean isPlayerTurn(){
        return isBlackTurn == isOpponentBlack;
    }

    protected void initializeMetaBoard(){
        for(int i = 0; i < 15; i++){
            metaBoard[i] = -1;
        }
        for(int i = 15; i <= 195; i += 15){
            metaBoard[i] = -1;
            for(int j = i + 1; j < i + 14; j++){
                metaBoard[j] = 0;
            }
            metaBoard[i + 14] = -1;
        }
        for(int i = 210; i < 225; i++){
            metaBoard[i] = -1;
        }
    }

    protected int convertMetaToReal(int metaPosition){
        // return -1 if metaPosition is not a valid space on the real board
        if(metaPosition < 15){
            return -1;
        }
        if(metaPosition > 210){
            return -1;
        }
        if(metaPosition % 15 == 0 || metaPosition % 15 == 14){
            return -1;
        }

        int realPosition = 0;
        realPosition = (metaPosition - 16) - 2 * (metaPosition / 15 - 1);
        return realPosition;
    }

    protected int convertRealToMeta(int realPosition){
        int metaPosition = 16;
        metaPosition += (realPosition + 2 * (realPosition / 13));
        return metaPosition;
    }

    protected int convertRealOffsetToMeta(int realOffset){
        int i = 0;
        while(OffsetConstants.REAL_OFFSETS[i] != realOffset){
            i++;
        }
        if(i < 8){
            return OffsetConstants.META_OFFSETS[i];
        }
        return 0;
    }

    protected boolean isMoveValid(int position){
        if(position >= 0 && position <= 168 && getCellValue(position) == 0){
            return true;
        }
        return false;
    }

    protected boolean isMoveValid(int origin, int offset){
        int position = origin + offset;
        if(!isMoveValid(position)){
            return false;
        }
        int metaPosition = convertRealToMeta(origin) + convertRealOffsetToMeta(offset);
        if(metaBoard[metaPosition] == -1){
            return false;
        }else{  
            return true;
        }
    }

    public boolean equals(Object obj){
        if(obj instanceof VirtualBoard){
            VirtualBoard other = (VirtualBoard) obj;
            for(int i = 0; i < 169; i++){
                if(this.getCellValue(i) != other.getCellValue(i)){
                    return false;
                }
            }
            return this.isBlackTurn == other.isBlackTurn && this.isOpponentBlack == other.isOpponentBlack;
        }
        return false;
    }

}
