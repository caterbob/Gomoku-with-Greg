package GomokuAIproject.Greg2;

import java.util.ArrayList;
import java.util.Arrays;

import GomokuAIproject.Board;
import GomokuAIproject.EngineHelpers.VirtualBoard;
import GomokuAIproject.Constants.BoardConstants;
import GomokuAIproject.Constants.EvaluationConstants;
import GomokuAIproject.Constants.OffsetConstants;

public class G2VirtualBoard extends VirtualBoard{

    private ArrayList<Integer> moveHistory;

    public G2VirtualBoard(boolean isOpponentBlack){
        super(Board.getInstance(), isOpponentBlack);
        moveHistory = new ArrayList<Integer>();
    }

    public G2VirtualBoard(G2VirtualBoard toCopy){
        super(toCopy);
        this.moveHistory = toCopy.getMoveHistory();
    }

    public void setIsOpponentBlack(boolean isOpponentBlack){
        super.isOpponentBlack = isOpponentBlack;
    }

    public ArrayList<Integer> getMoveHistory(){
        return moveHistory;
    }

    public void sync(){
        super.sync();
        clearMoveHistory();
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
    public int[][] getEvaluation(){ // returns evaluation in 1st element of 1st array, then returns other threat arrays for move ordering
        double evaluation = 0;
        int[] line = new int[5];
        int previousSpace = -1;
        int afterSpace = -1;
        int[] patternCounts = new int[9];

        // for 4 stones that can become 5
        int[] whiteImmediateThreats = new int[169];
        int[] blackImmediateThreats = new int[169];

        int[] whiteImmediateThreatIndex = new int[1];   //int[] used to bypass pass-by-value
        int[] blackImmediateThreatIndex = new int[1];

        // for open 3's that could become an open 4
        int[] whiteOpen4Threats = new int[169];
        int[] blackOpen4Threats = new int[169];

        int[] whiteOpen4ThreatIndex = new int[1];    // int[] used to bypass pass-by-value
        int[] blackOpen4ThreatIndex = new int[1];

        // for any 3 that can become any 4
        int[] whiteThreats = new int[338];
        int[] blackThreats = new int[338];

        int[] whiteThreatIndex = new int[1];
        int[] blackThreatIndex = new int[1];

        // horizontal screening
        for(int rowStart = 0; rowStart <= 156; rowStart += 13){
            previousSpace = -1;
            afterSpace = -1;
            for(int i = 0; i < 9; i++){
                int lineStart = rowStart + i;
                for(int lineIndex = 0; lineIndex < 5; lineIndex++){
                    line[lineIndex] = board[lineStart + lineIndex];
                }
                if(i != 8)
                    afterSpace = board[lineStart + 5];

                int[] result = updatePatternCounts(line, lineStart, 1, patternCounts, previousSpace, afterSpace);
                handlePatternCountsResult(result, blackImmediateThreats, blackImmediateThreatIndex, 
                whiteImmediateThreats, whiteImmediateThreatIndex, blackOpen4Threats, blackOpen4ThreatIndex, 
                whiteOpen4Threats, whiteOpen4ThreatIndex, blackThreats, blackThreatIndex, whiteThreats, whiteThreatIndex);
                
                if(patternCounts[EvaluationConstants.G2.WIN_PRESENT] != 0){
                    if(line[0] == BoardConstants.BLACK)
                        return new int[][]{{-9999}};
                    return new int[][]{{9999}};
                }
                previousSpace = line[0];
            }
        }

        // vertical screening
        for(int colStart = 0; colStart < 13; colStart++){
            previousSpace = -1;
            afterSpace = -1;
            for(int i = 0; i < 9; i++){
                int lineStart = colStart + i * 13;
                for(int lineIndex = 0; lineIndex < 5; lineIndex++){
                    line[lineIndex] = board[lineStart + lineIndex * 13];
                }
                if(i != 8)
                    afterSpace = board[lineStart + 65];

                int[] result = updatePatternCounts(line, lineStart, 13, patternCounts, previousSpace, afterSpace);
                handlePatternCountsResult(result, blackImmediateThreats, blackImmediateThreatIndex, 
                whiteImmediateThreats, whiteImmediateThreatIndex, blackOpen4Threats, blackOpen4ThreatIndex, 
                whiteOpen4Threats, whiteOpen4ThreatIndex, blackThreats, blackThreatIndex, whiteThreats, whiteThreatIndex);

                if(patternCounts[EvaluationConstants.G2.WIN_PRESENT] != 0){
                    if(line[0] == BoardConstants.BLACK)
                        return new int[][]{{-9999}};
                    return new int[][]{{9999}};
                }
                previousSpace = line[0];
            }
        }

        // forward diagonal screening (/)
        for(int diagStart = 52; diagStart <= 156; diagStart += 13){
            previousSpace = -1;
            afterSpace = -1;
            for(int i = 0; i < ((diagStart-39)/13); i++){
                int lineStart = diagStart - i * 12;
                for(int lineIndex = 0; lineIndex < 5; lineIndex++){
                    line[lineIndex] = board[lineStart - lineIndex * 12];
                }
                if(i != ((diagStart-39)/13)-1)
                    afterSpace = board[lineStart - 60];

                int[] result = updatePatternCounts(line, lineStart, -12, patternCounts, previousSpace, afterSpace);
                handlePatternCountsResult(result, blackImmediateThreats, blackImmediateThreatIndex, 
                whiteImmediateThreats, whiteImmediateThreatIndex, blackOpen4Threats, blackOpen4ThreatIndex, 
                whiteOpen4Threats, whiteOpen4ThreatIndex, blackThreats, blackThreatIndex, whiteThreats, whiteThreatIndex);

                if(patternCounts[EvaluationConstants.G2.WIN_PRESENT] != 0){
                    if(line[0] == BoardConstants.BLACK)
                        return new int[][]{{-9999}};
                    return new int[][]{{9999}};
                }
                previousSpace = line[0];
            }
        }
        for(int diagStart = 157; diagStart <= 164; diagStart++){
            previousSpace = -1;
            afterSpace = -1;
            for(int i = 0; i < (164-diagStart+1); i++){
                int lineStart = diagStart - i * 12;
                for(int lineIndex = 0; lineIndex < 5; lineIndex++){
                    line[lineIndex] = board[lineStart - lineIndex * 12];
                }
                if(i != (164-diagStart))
                    afterSpace = board[lineStart - 60];

                int[] result = updatePatternCounts(line, lineStart, -12, patternCounts, previousSpace, afterSpace);
                handlePatternCountsResult(result, blackImmediateThreats, blackImmediateThreatIndex, 
                whiteImmediateThreats, whiteImmediateThreatIndex, blackOpen4Threats, blackOpen4ThreatIndex, 
                whiteOpen4Threats, whiteOpen4ThreatIndex, blackThreats, blackThreatIndex, whiteThreats, whiteThreatIndex);

                if(patternCounts[EvaluationConstants.G2.WIN_PRESENT] != 0){
                    if(line[0] == BoardConstants.BLACK)
                        return new int[][]{{-9999}};
                    return new int[][]{{9999}};
                }
                previousSpace = line[0];
            }
        }

        // backward diagonal screening (\)
        for(int diagStart = 64; diagStart <= 168; diagStart += 13){
            previousSpace = -1;
            afterSpace = -1;
            for(int i = 0; i < ((diagStart-51)/13); i++){
                int lineStart = diagStart - i * 14;
                for(int lineIndex = 0; lineIndex < 5; lineIndex++){
                    line[lineIndex] = board[lineStart - lineIndex * 14];
                }
                if(i != ((diagStart-51)/13)-1)
                    afterSpace = board[lineStart - 70];

                int result[] = updatePatternCounts(line, lineStart, -14,  patternCounts, previousSpace, afterSpace);
                handlePatternCountsResult(result, blackImmediateThreats, blackImmediateThreatIndex, 
                whiteImmediateThreats, whiteImmediateThreatIndex, blackOpen4Threats, blackOpen4ThreatIndex, 
                whiteOpen4Threats, whiteOpen4ThreatIndex, blackThreats, blackThreatIndex, whiteThreats, whiteThreatIndex);

                if(patternCounts[EvaluationConstants.G2.WIN_PRESENT] != 0){
                    if(line[0] == BoardConstants.BLACK)
                        return new int[][]{{-9999}};
                    return new int[][]{{9999}};
                }
                previousSpace = line[0];
            }
        }
        for(int diagStart = 167; diagStart >= 160; diagStart--){
            previousSpace = -1;
            afterSpace = -1;
            for(int i = 0; i < (diagStart-160+1); i++){
                int lineStart = diagStart - i * 14;
                for(int lineIndex = 0; lineIndex < 5; lineIndex++){
                    line[lineIndex] = board[lineStart - lineIndex * 14];
                }
                if(i != (diagStart-160))
                    afterSpace = board[lineStart - 70];

                int result[] = updatePatternCounts(line, lineStart, -14, patternCounts, previousSpace, afterSpace);
                handlePatternCountsResult(result, blackImmediateThreats, blackImmediateThreatIndex, 
                whiteImmediateThreats, whiteImmediateThreatIndex, blackOpen4Threats, blackOpen4ThreatIndex, 
                whiteOpen4Threats, whiteOpen4ThreatIndex, blackThreats, blackThreatIndex, whiteThreats, whiteThreatIndex);

                if(patternCounts[EvaluationConstants.G2.WIN_PRESENT] != 0){
                    if(line[0] == BoardConstants.BLACK)
                        return new int[][]{{-9999}};
                    return new int[][]{{9999}};
                }
                previousSpace = line[0];
            }
        }

        if(isDraw())
            return new int[][]{{EvaluationConstants.GAME_DRAWN}, blackThreats, blackThreatIndex, 
            whiteThreats, whiteThreatIndex, blackImmediateThreats, blackImmediateThreatIndex, whiteImmediateThreats, whiteImmediateThreatIndex};
        int winningComboScore = getWinningComboScore(patternCounts, whiteImmediateThreatIndex, blackImmediateThreatIndex, 
            whiteOpen4ThreatIndex, blackOpen4ThreatIndex, whiteThreatIndex, blackThreatIndex, whiteImmediateThreats, 
            blackImmediateThreats, whiteOpen4Threats, blackOpen4Threats, whiteThreats, blackThreats);
        if(Math.abs(winningComboScore) == EvaluationConstants.GAME_WILL_BE_OVER){
            return new int[][]{{winningComboScore}, blackThreats, blackThreatIndex, 
            whiteThreats, whiteThreatIndex, blackImmediateThreats, blackImmediateThreatIndex, whiteImmediateThreats, whiteImmediateThreatIndex};
        }
        return new int[][]{{winningComboScore + getPatternCountValues(patternCounts)}, blackThreats, blackThreatIndex, 
            whiteThreats, whiteThreatIndex, blackImmediateThreats, blackImmediateThreatIndex, whiteImmediateThreats, whiteImmediateThreatIndex};
    }

    //helper function of evaluation
    // returns true if an index needs to be incremented
    private void handlePatternCountsResult(int[] result, int[] blackImmediateThreats, int[] blackImmediateThreatIndex,
    int[] whiteImmediateThreats, int[] whiteImmediateThreatIndex, int[] blackOpen4Threats, int[] blackOpen4ThreatIndex,
    int[] whiteOpen4Threats, int[] whiteOpen4ThreatIndex, int[] blackThreats, int[] blackThreatIndex, int[] whiteThreats, int[] whiteThreatIndex){
        for(int location: result){
            if(location != -1){
                if(location <= 168){  // black immediate threat
                    if(updateThreats(blackImmediateThreats, location, blackImmediateThreatIndex, false))
                        blackImmediateThreatIndex[0]++;
                }else if (location <= 337){   // white immediate threat
                    if(updateThreats(whiteImmediateThreats, location - 169, whiteImmediateThreatIndex, false))
                        whiteImmediateThreatIndex[0]++;
                }else if (location <= 506){   // black open 4 threat
                    if(updateThreats(blackOpen4Threats, location - 338, blackOpen4ThreatIndex, false))
                        blackOpen4ThreatIndex[0]++;
                    if(updateThreats(blackThreats, location - 338, blackThreatIndex, false))
                        blackThreatIndex[0]++;
                }else if (location <= 675){ // white open 4 threat
                    if(updateThreats(whiteOpen4Threats, location - 507, whiteOpen4ThreatIndex, false))
                        whiteOpen4ThreatIndex[0]++;
                    if(updateThreats(whiteThreats, location - 507, whiteThreatIndex, false)){
                        whiteThreatIndex[0]++;
                        ////System.out.println("test print 1");
                    }
                }else if(location <= 844){  // black threat to make any 4
                    if(updateThreats(blackThreats, location - 676, blackThreatIndex, false)){
                        blackThreatIndex[0]++;
                    }
                }else{  // white threat to make any 4
                    if(updateThreats(whiteThreats, location - 845, whiteThreatIndex, false)){
                        whiteThreatIndex[0]++;
                        ////System.out.println("test print 2");
                    }
                }
            }
        }
    }

    // helper function of evaluation
    private int getPatternCountValues(int[] patternCounts){
        int evaluation = 0;
        evaluation -= patternCounts[EvaluationConstants.G2.BLACK_LINE_OF_2] * EvaluationConstants.G2.LINE_OF_2_VALUE;
        evaluation += patternCounts[EvaluationConstants.G2.WHITE_LINE_OF_2] * EvaluationConstants.G2.LINE_OF_2_VALUE;
        evaluation -= (patternCounts[EvaluationConstants.G2.BLACK_LINE_OF_3] + patternCounts[EvaluationConstants.G2.BLACK_SPECIAL_3]) * EvaluationConstants.G2.LINE_OF_3_VALUE;
        evaluation += (patternCounts[EvaluationConstants.G2.WHITE_LINE_OF_3] + patternCounts[EvaluationConstants.G2.WHITE_SPECIAL_3]) * EvaluationConstants.G2.LINE_OF_3_VALUE;
        evaluation -= patternCounts[EvaluationConstants.G2.BLACK_LINE_OF_4] * EvaluationConstants.G2.LINE_OF_4_VALUE;
        evaluation += patternCounts[EvaluationConstants.G2.WHITE_LINE_OF_4] * EvaluationConstants.G2.LINE_OF_4_VALUE;
        return evaluation;
    }

    // helper function of evaluation
    // returns location of a threat (win on next turn), -1 if none found
    private int[] updatePatternCounts(int[] line, int locationStart, int offset, int[] patternCounts, int previousSpace, int afterSpace){
        // get counts from line
        int blackCount = 0;
        int whiteCount = 0;
        int emptyCount = 0;
        int emptyLocation = -1; // for use in line of 4 recognition
        int lastEmptyLocation = -1; // use for line of 3 recognition
        for(int i = 0; i < 5; i++){
            int cell = line[i];
            if(cell == BoardConstants.EMPTY){
                if(emptyLocation != -1)
                    lastEmptyLocation = emptyLocation;
                emptyLocation = locationStart + i * offset;
                emptyCount++;
            }
            else if(cell == BoardConstants.BLACK)
                blackCount++;
            else
                whiteCount++;
        }

        // pattern recognition
        if(emptyCount >= 4){
            return new int[]{-1};
        }
        if(blackCount > 0 && whiteCount > 0){
            return new int[]{-1};
        }
        if(blackCount > 0){    // black stone combos
            if(blackCount == 2){
                patternCounts[EvaluationConstants.G2.BLACK_LINE_OF_2]++;
                return new int[]{-1};
            }else if(blackCount == 3){
                if(line[0] == BoardConstants.EMPTY && line[4] == BoardConstants.EMPTY
                && (previousSpace == BoardConstants.EMPTY || afterSpace == BoardConstants.EMPTY)){ // check if three can become an open
                    if(previousSpace != BoardConstants.BLACK && afterSpace != BoardConstants.BLACK){
                        patternCounts[EvaluationConstants.G2.BLACK_SPECIAL_3]++;
                        int[] temp = {-1, -1, -1, -1};
                        if(previousSpace == BoardConstants.EMPTY)
                            temp[0] = locationStart + 169 * 2;  // add 169*2 to distinguish as black open 3 threat
                        if(afterSpace == BoardConstants.EMPTY)
                            temp[1] = locationStart + 4 * offset + 169 * 2;
                        temp[2] = locationStart + 169 * 4;
                        temp[3] = locationStart + 4 * offset + 169 * 4;
                        return temp;
                    }   // not special 3
                    patternCounts[EvaluationConstants.G2.BLACK_LINE_OF_3]++;
                    return new int[]{locationStart + 169 * 4, locationStart + 4 * offset + 169 * 4};
                }else{
                    patternCounts[EvaluationConstants.G2.BLACK_LINE_OF_3]++;
                    return new int[]{lastEmptyLocation + 169 * 4, emptyLocation + 169 * 4};
                }
            }else if(blackCount == 4){
                patternCounts[EvaluationConstants.G2.BLACK_LINE_OF_4]++;
                return new int[]{emptyLocation};
            }else{
                patternCounts[EvaluationConstants.G2.WIN_PRESENT]++;
                return new int[]{-1};
            }
        }else{  // must be a white stone combo
            if(whiteCount == 2){
                patternCounts[EvaluationConstants.G2.WHITE_LINE_OF_2]++;
                return new int[]{-1};
            }else if(whiteCount == 3){
                if(line[0] == BoardConstants.EMPTY && line[4] == BoardConstants.EMPTY
                && (previousSpace == BoardConstants.EMPTY || afterSpace == BoardConstants.EMPTY)){ // check if three can become an open
                    if(previousSpace != BoardConstants.WHITE && afterSpace != BoardConstants.WHITE){
                        patternCounts[EvaluationConstants.G2.WHITE_SPECIAL_3]++;
                        int[] temp = {-1, -1, -1, -1};
                        if(previousSpace == BoardConstants.EMPTY)
                            temp[0] = locationStart + 169 * 3;  // add 169*3 to distinguish as white open 3 threat
                        if(afterSpace == BoardConstants.EMPTY)
                            temp[1] = locationStart + 4 * offset + 169 * 3;
                        temp[2] = locationStart + 169 * 5;
                        temp[3] = locationStart + 4 * offset + 169 * 5;
                        return temp;
                    }   // not special 3
                    patternCounts[EvaluationConstants.G2.WHITE_LINE_OF_3]++;
                    return new int[]{locationStart + 169 * 5, locationStart + 4 * offset + 169 * 5};
                }else{
                    patternCounts[EvaluationConstants.G2.WHITE_LINE_OF_3]++;
                    return new int[]{lastEmptyLocation + 169 * 5, emptyLocation + 169 * 5};
                }
            }else if(whiteCount == 4){
                patternCounts[EvaluationConstants.G2.WHITE_LINE_OF_4]++;
                return new int[]{emptyLocation + 169}; // differentiates white from black threats
            }else{
                patternCounts[EvaluationConstants.G2.WIN_PRESENT]++;
                return new int[]{-1};
            }
        }
    }

    // helper function of eval
    // returns true if successful update occurs, false if no update occurs
    private boolean updateThreats(int[] threatList, int threatLocation, int[] threatListIndex, boolean allowDuplicates){
        // //System.out.println("threat location: " + threatLocation);
        // //System.out.println("threat list index: " + threatListIndex[0]);
        if(allowDuplicates){
            threatList[threatListIndex[0]] = threatLocation;
            return true;
        }
        boolean found = false;
        for(int i = 0; i < threatListIndex[0]; i++){
            if(threatList[i] == threatLocation){
                found = true;
                break;
            }
        }
        if(found)
            return false;   //otherwise vvv
        threatList[threatListIndex[0]] = threatLocation;
        ////System.out.println("in update threats: " + //System.identityHashCode(threatList));
        return true;
    }

    // helper function of getWinningComboScore
    private boolean hasOverlap(int[] listA, int[] listB, int sizeA, int sizeB) {
        //System.out.println();
        for(int i = 0; i < sizeA; i++){
            //System.out.print(listA[i] + " ");
        }
        //System.out.println();
        for(int i = 0; i < sizeB; i++){
            //System.out.print(listB[i] + " ");
        }
        for (int i = 0; i < sizeA; i++) {
            for (int j = 0; j < sizeB; j++) {
                if (listA[i] == listB[j]) {
                    return true;  // overlap found
                }
            }
        }
        return false;  // no overlap found
    }

    // helper function of eval
    private int getWinningComboScore(int[] patternCounts, int[] whiteImmediateThreatIndex, int[] blackImmediateThreatIndex,
    int[] whiteOpen4ThreatIndex, int[] blackOpen4ThreatIndex, int[] whiteThreatIndex, int[] blackThreatIndex, 
    int[] whiteImmediateThreats, int[] blackImmediateThreats, int[] whiteOpen4Threats, int[] blackOpen4Threats, 
    int[] whiteThreats, int[] blackThreats){

        if(isBlackTurn && patternCounts[EvaluationConstants.G2.BLACK_LINE_OF_4] > 0){
            //System.out.println("Black gets 5 next turn!");
            return -9998;
        }
        if(!isBlackTurn && patternCounts[EvaluationConstants.G2.WHITE_LINE_OF_4] > 0){
            //System.out.println("White gets 5 next turn!");
            return 9998;
        }
        if(blackImmediateThreatIndex[0] >= 2){
            //System.out.println("White can't stop both black's 5-threats!");
            return -9998;
        }
        if(whiteImmediateThreatIndex[0] >= 2){
            //System.out.println("Black can't stop both white's 5-threats!");
            return 9998;
        }
        if(isBlackTurn && patternCounts[EvaluationConstants.G2.BLACK_SPECIAL_3] > 0 && whiteImmediateThreatIndex[0] == 0){
            //System.out.println("Black gets an open 4!");
            return -9998;
        }
        if(!isBlackTurn && patternCounts[EvaluationConstants.G2.WHITE_SPECIAL_3] > 0 && blackImmediateThreatIndex[0] == 0){
            //System.out.println("White gets an open 4!");
            return 9998;
        }
        if(!isBlackTurn && patternCounts[EvaluationConstants.G2.BLACK_SPECIAL_3] > 0 && patternCounts[EvaluationConstants.G2.BLACK_LINE_OF_4] > 0
        && !hasOverlap(blackOpen4Threats, blackImmediateThreats, blackOpen4ThreatIndex[0], blackImmediateThreatIndex[0])
        && !hasOverlap(whiteThreats, blackImmediateThreats, whiteThreatIndex[0], blackImmediateThreatIndex[0])){
            //System.out.println("White can't stop both the 5-threat and open-4 threat!");
            return -9998;
        }
        if(isBlackTurn && patternCounts[EvaluationConstants.G2.WHITE_SPECIAL_3] > 0 && patternCounts[EvaluationConstants.G2.WHITE_LINE_OF_4] > 0
        && !hasOverlap(whiteOpen4Threats, whiteImmediateThreats, whiteOpen4ThreatIndex[0], whiteImmediateThreatIndex[0])
        && !hasOverlap(blackThreats, whiteImmediateThreats, blackThreatIndex[0], whiteImmediateThreatIndex[0])){
            //System.out.println("Black can't stop both the 5-threat and open-4 threat!");
            return 9998;
        }
        if(blackOpen4ThreatIndex[0] > 1 && patternCounts[EvaluationConstants.G2.BLACK_SPECIAL_3] > 1 
        && !hasOverlap(blackOpen4Threats, whiteThreats, blackOpen4ThreatIndex[0], whiteThreatIndex[0])  // likely win
        ){ // two open 3s
            //System.out.println("White struggles to stop black making an open 4!");
            return -20;
        }
        if(whiteOpen4ThreatIndex[0] > 1 && patternCounts[EvaluationConstants.G2.WHITE_SPECIAL_3] > 1 
        && !hasOverlap(whiteOpen4Threats, blackThreats, whiteOpen4ThreatIndex[0], blackThreatIndex[0])){  // likely win 
            //System.out.println("Black struggles to stop white making an open 4!");
            return 20;
        }
        return 0;
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
