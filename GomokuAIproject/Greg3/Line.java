package GomokuAIproject.Greg3;

import GomokuAIproject.EngineHelpers.VirtualBoard;

public class Line {

    private VirtualBoard virtualBoard;
    private int evaluation;
    private int[] myCells;
    private boolean fix;

    // Threat Maps
    private LocationList black5Threats;
    private LocationList white5Threats;
    private LocationList blackOpen4Threats;
    private LocationList whiteOpen4Threats;
    private LocationList black4Threats;
    private LocationList white4Threats;
    private LocationList black3Threats;
    private LocationList white3Threats;
    private LocationList[] threatList;

    private int numberOfSpecial3;
    private int[] segmentScoreTableUsed;

    public Line(VirtualBoard virtualBoard, int[] cells, boolean testSegmentScores){   // pass in virtual board the cells in the line
        this.virtualBoard = virtualBoard;
        evaluation = 0;
        myCells = cells;
        black5Threats = new LocationList();
        white5Threats = new LocationList();
        blackOpen4Threats = new LocationList();
        whiteOpen4Threats = new LocationList();
        black4Threats = new LocationList();
        white4Threats = new LocationList();
        black3Threats = new LocationList();
        white3Threats = new LocationList();
        threatList = new LocationList[]{
            black5Threats,
            blackOpen4Threats,
            black4Threats,
            black3Threats,
            white5Threats,
            whiteOpen4Threats,
            white4Threats,
            white3Threats
        };
        numberOfSpecial3 = 0;
        // if(testSegmentScores){
        //     segmentScoreTableUsed = G3Constants.TEST_SEGMENT_SCORE_TABLE;
        // }else{
            segmentScoreTableUsed = G3Constants.SEGMENT_SCORE_TABLE;
        //}
        fix = testSegmentScores;
    }

    // gets current evaluation (without modification)
    public int getEvaluation(){
        return evaluation;
    }

    // modifies and returns evaluation of the line (global variable)
    public int evaluateLine(){
        evaluation = 0;
        clearThreatList();
        // masks are 7 bit integers representing: (space to the left of segment, space to the right of segment, 1st stone, 2nd stone, 3rd stone, 4th stone, 5th stone)
        int whiteMask = 0b0;
        int blackMask = 0b0;

        // initialize masks with first segment of the line
        for(int i = 1; i < 6; i++){
            int cellValue = virtualBoard.getCellValue(myCells[i]);
            blackMask <<= 1;
            blackMask |= (cellValue & 1);
            whiteMask <<= 1;
            whiteMask |= (cellValue >> 1);
        }
        int leftCellValue = virtualBoard.getCellValue(myCells[0]);
        int rightCellValue = virtualBoard.getCellValue(myCells[6]);
        blackMask |= ((leftCellValue & 1) << 6); // add left space
        blackMask |= ((rightCellValue & 1) << 5); // add right space
        whiteMask |= ((leftCellValue >> 1) << 6); // add left space
        whiteMask |= ((rightCellValue >> 1) << 5); // add right space

        // evaluate initial mask segments
        if((whiteMask & 0b11111) == 0){
            evaluation -= segmentScoreTableUsed[blackMask & 0b11111];
            updateThreatMaps(transformSegment(blackMask, whiteMask), 6, true);
        }else if((blackMask & 0b11111) == 0){
            evaluation += segmentScoreTableUsed[whiteMask & 0b11111];
            updateThreatMaps(transformSegment(whiteMask, blackMask), 6, false);
        }

        // iterate through rest of segments in line
        for(int i = 7; i < myCells.length; i++){
            blackMask = updateSegmentMask(blackMask, virtualBoard.getCellValue(myCells[i]) & 1);
            whiteMask = updateSegmentMask(whiteMask, virtualBoard.getCellValue(myCells[i]) >> 1);
            if((whiteMask & 0b11111) == 0){ // if first five bits (actual segment) is empty
                evaluation -= segmentScoreTableUsed[blackMask & 0b11111]; 
                updateThreatMaps(transformSegment(blackMask, whiteMask), i, true);
            }else if((blackMask & 0b11111) == 0){
                evaluation += segmentScoreTableUsed[whiteMask & 0b11111];
                updateThreatMaps(transformSegment(whiteMask, blackMask), i, false);
            }
        }
        

        return evaluation;
    }

    // returns next black segment based on last one and next cellValue bit
    private int updateSegmentMask(int previousSegment, int nextBit){
        previousSegment <<= 1;
        previousSegment |= (previousSegment >> 6 & 1);
        previousSegment &= ~(1 << 6);
        previousSegment |= (previousSegment >> 5 & 1) << 6;
        previousSegment &= ~(1 << 5);
        previousSegment |= (nextBit << 5);
        return previousSegment &= ~(1 << 7);
    }

    // transforms 7 bit mask into new 7 bit mask for use in updateThreatMaps
    // primary segment is the one being evaluated (secondarySegment used for open 4 context)
    private int transformSegment(int primarySegment, int secondarySegment){
        return (primarySegment | ((secondarySegment >> 5) << 5));
    }

    // index = looping variable i in evaluateLine()
    private void updateThreatMaps(int transformedSegment, int index, boolean forBlack){
        int[] threatTableEntry = G3Constants.SEGMENT_THREAT_TABLE[transformedSegment];
        int threatCode = threatTableEntry[0];
        int offset = (forBlack)? 0: 4;  // adjusts which index to use in threatList

        if(threatCode == G3Constants.OPEN_FOUR_THREAT_CODE){
            threatList[G3Constants.OPEN_FOUR_THREAT_INDEX + offset].addLocation(myCells[index + threatTableEntry[1]]);
            if(threatTableEntry[2] != G3Constants.NONEXISTENT_CODE)
                threatList[G3Constants.OPEN_FOUR_THREAT_INDEX + offset].addLocation(myCells[index + threatTableEntry[2]]);
            for(int i = 3; i < threatTableEntry.length; i++){
                threatList[G3Constants.FOUR_THREAT_INDEX + offset].addLocation(myCells[index + threatTableEntry[i]]);
            }
        }else if(threatCode == G3Constants.FIVE_THREAT_CODE){
            for(int i = 1; i < threatTableEntry.length; i++){
                threatList[G3Constants.FIVE_THREAT_INDEX + offset].addLocation(myCells[index + threatTableEntry[i]]);
            }
        }else if(threatCode == G3Constants.FOUR_THREAT_CODE){
            for(int i = 1; i < threatTableEntry.length; i++){
                threatList[G3Constants.FOUR_THREAT_INDEX + offset].addLocation(myCells[index + threatTableEntry[i]]);
            }
        }
        else if(threatCode == G3Constants.THREE_THREAT_CODE){
            for(int i = 1; i < threatTableEntry.length; i++){
                threatList[G3Constants.THREE_THREAT_INDEX + offset].addLocation(myCells[index + threatTableEntry[i]]);
            }
        }
    }

    public LocationList getThreatMap(int threatListIndex){
        return threatList[threatListIndex];
    }

    public void clearThreatList(){
        for(LocationList threatMap: threatList){
            threatMap.clear();
        }
    }

}
