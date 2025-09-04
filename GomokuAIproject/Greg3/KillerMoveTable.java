package GomokuAIproject.Greg3;

import java.util.Arrays;

public class KillerMoveTable {

    private int[][] table;
    private static final int NO_MOVE_STORED = -1;
    private int depthLimit;
    private int moveStored;

    public KillerMoveTable(int depthLimit, int movesStored){
        this.depthLimit = depthLimit;
        this.moveStored = movesStored;
        table = new int[depthLimit][movesStored];
        for(int[] moveList: table){
            Arrays.fill(moveList, NO_MOVE_STORED);
        }
    }

    // MUST BE USED AS READ ONLY IF USED OUTSIDE OF CLASS
    public int[] getKillers(int plyFromRoot){
        // depth subtracted by 1 to account for 0-indexing difference
        return table[plyFromRoot];
    }

    // adds killer move
    public void addKillerMove(int move, int plyFromRoot){
        int[] moveList = getKillers(plyFromRoot);
        for(int i = moveList.length - 1; i >= 0; i--){
            if(moveList[i] == NO_MOVE_STORED || moveList[i] == move){
                moveList[i] = move;
                return;
            }
        }
        // if no available slot, shift killer moves and add new killer move at index 0
        for(int i = moveList.length - 1; i > 0; i--){
            moveList[i] = moveList[i - 1];
        }
        moveList[0] = move;
    }

    public void shiftBackward(){
        for(int i = 0; i < table.length - 1; i++){
            table[i] = table[i + 1];
        }
        int[] newMoveList = new int[moveStored];
        Arrays.fill(newMoveList, NO_MOVE_STORED);
        table[depthLimit - 1] = newMoveList;
    }

    public void clear(){
        table = new int[depthLimit][moveStored];
        for(int[] moveList: table){
            Arrays.fill(moveList, NO_MOVE_STORED);
        }
    }

}
