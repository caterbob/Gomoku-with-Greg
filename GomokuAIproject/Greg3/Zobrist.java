package GomokuAIproject.Greg3;
import java.util.Random;

import GomokuAIproject.EngineHelpers.VirtualBoard;

public class Zobrist {

    private static final long[][] zobristTable = new long[170][3];   // 169 spaces (+ 1 turn value), 3 possible values

    static {
        Random random = new Random(19);
        for(int location = 0; location < 169; location++){
            for(int value = 0; value < 3; value++){
                zobristTable[location][value] = random.nextLong();
            }
        }
    }

    public static long computeHash(VirtualBoard boardState){
        long hash = 0;
        for(int location = 0; location < 169; location++){
            hash ^= zobristTable[location][boardState.getCellValue(location)];
        }
        int turnValue = (boardState.isBlackTurn())? 1: 0;
        hash ^= zobristTable[169][turnValue];   // add turn to hash
        return hash;
    }

    // used to updateHash once a move has been made on the virtualBoard
    public static long updateHash(long currentHash, int location, int value, boolean isBlackTurn){
        int turnValue = (isBlackTurn)? 1: 0;
        currentHash ^= zobristTable[location][value];
        currentHash ^= zobristTable[169][turnValue];
        return currentHash;
    }
}
