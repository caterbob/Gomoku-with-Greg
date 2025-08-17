package GomokuAIproject.Greg3;

import GomokuAIproject.EngineHelpers.VirtualBoard;

public class LineGroup {

    private Line[] myLines;
    private int[] lineEvaluations;
    private int evaluation;
    private VirtualBoard virtualBoard;

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

    public LineGroup(VirtualBoard virtualBoard, Line[] lines){
        this.virtualBoard = virtualBoard;
        myLines = lines;
        lineEvaluations = new int[myLines.length];
        for(int i = 0; i < myLines.length; i++){
            lineEvaluations[i] = myLines[i].getEvaluation();
        }
        evaluation = 0;

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
    }

    // used to initialize if previous board history doesn't exist
    public int getEvaluation(){
        evaluation = 0;
        for(int i = 0; i < myLines.length; i++){
            int lineEvaluation = myLines[i].evaluateLine();
            evaluation += lineEvaluation;
            lineEvaluations[i] = lineEvaluation;
        }
        clearThreatList();
        getThreatList();
        int comboScore = getComboScore();
        if(Math.abs(comboScore) == G3Constants.GAME_WILL_BE_OVER){
            return comboScore;
        }
        return evaluation + comboScore;
    }

    // assumes LineGroup consists of all 60 lines on board
    public int updateEvaluation(int locationOfStone){
        clearThreatList();
        // find indexes of lines that need to be updated
        int rowToChange = (locationOfStone / 13);
        int columnToChange = (locationOfStone % 13) + 13;
        int forwardDiagonalToChange;
        if(rowToChange + columnToChange - 13 < 12){
            forwardDiagonalToChange = locationOfStone % 12 - 4 + 26;
        }else{
            forwardDiagonalToChange = locationOfStone % 12 + 8 + 26;
        }
        int backwardDiagonalToChange;
        if(columnToChange - 13 >= rowToChange){
            backwardDiagonalToChange = (8 - locationOfStone % 14) + 43;
        }else{
            backwardDiagonalToChange = (22 - locationOfStone % 14) + 43;
        }
    
        int lineEvaluation;
        // if line exists, subtract old eval and add new eval
        if(rowToChange >= 0 && rowToChange <= 12){
            evaluation -= lineEvaluations[rowToChange];
            lineEvaluation = myLines[rowToChange].evaluateLine();
            evaluation += lineEvaluation;
            lineEvaluations[rowToChange] = lineEvaluation;
            //updateThreatList(rowToChange);
        }
        if(columnToChange >= 13 && columnToChange <= 25){
            evaluation -= lineEvaluations[columnToChange];
            lineEvaluation = myLines[columnToChange].evaluateLine();
            evaluation += lineEvaluation;
            lineEvaluations[columnToChange] = lineEvaluation;
            //updateThreatList(columnToChange);
        }
        if(forwardDiagonalToChange >= 26 && forwardDiagonalToChange <= 42){
            evaluation -= lineEvaluations[forwardDiagonalToChange];
            lineEvaluation = myLines[forwardDiagonalToChange].evaluateLine();
            evaluation += lineEvaluation;
            lineEvaluations[forwardDiagonalToChange] = lineEvaluation;
            //updateThreatList(forwardDiagonalToChange);
        }
        if(backwardDiagonalToChange >= 43 && backwardDiagonalToChange <= 59){
            evaluation -= lineEvaluations[backwardDiagonalToChange];
            lineEvaluation = myLines[backwardDiagonalToChange].evaluateLine();
            evaluation += lineEvaluation;
            lineEvaluations[backwardDiagonalToChange] = lineEvaluation;
            //updateThreatList(backwardDiagonalToChange);
        }
        getThreatList();
        int comboScore = getComboScore();
        if(Math.abs(comboScore) == G3Constants.GAME_WILL_BE_OVER){
            return comboScore;
        }
        if(Math.abs(evaluation) > 50000){
            return G3Constants.GAME_OVER * (int)Math.signum(evaluation);
        }
        return evaluation + comboScore;
    }

    public int getComboScore(){
        if(virtualBoard.isBlackTurn() && black5Threats.getSize() > 0){
            return -G3Constants.GAME_WILL_BE_OVER;
        }
        if(!virtualBoard.isBlackTurn() && white5Threats.getSize() > 0){
            return G3Constants.GAME_WILL_BE_OVER;
        }
        // if(black5Threats.getSize() >= 2){
        //     return -G3Constants.GAME_WILL_BE_OVER;
        // }
        // if(white5Threats.getSize() >= 2){
        //     return G3Constants.GAME_WILL_BE_OVER;
        // }
        // if(virtualBoard.isBlackTurn() && blackOpen4Threats.getSize() > 0 && white5Threats.getSize() == 0){
        //     return -G3Constants.GAME_WILL_BE_OVER;
        // }
        // if(!virtualBoard.isBlackTurn() && whiteOpen4Threats.getSize() > 0 && black5Threats.getSize() == 0){
        //     return G3Constants.GAME_WILL_BE_OVER;
        // }
        // if(!virtualBoard.isBlackTurn() && blackOpen4Threats.getSize() > 0 && black5Threats.getSize() > 0
        // && !blackOpen4Threats.hasOverlap(black5Threats) && !white4Threats.hasOverlap(black5Threats)){  // should technically be black4Threats for first overlap check
        //     return -G3Constants.GAME_WILL_BE_OVER;
        // }
        // if(virtualBoard.isBlackTurn() && whiteOpen4Threats.getSize() > 0 && white5Threats.getSize() > 0
        // && !whiteOpen4Threats.hasOverlap(white5Threats) && !black4Threats.hasOverlap(white5Threats)){  // should technically be black4Threats for first overlap check
        //     return G3Constants.GAME_WILL_BE_OVER;
        // }
        // if(blackOpen4Threats.getSize() > 2 && !blackOpen4Threats.hasOverlap(white4Threats)){
        //     return -200;
        // }
        // if(whiteOpen4Threats.getSize() > 2 && !whiteOpen4Threats.hasOverlap(black4Threats)){
        //     return 200;
        // }
        return 0;
    }

    // thoroughly updates the entire threatList
    private void getThreatList(){
        for(Line line: myLines){
            for(int i = 0; i < threatList.length; i++){
                threatList[i].combine(line.getThreatMap(i));
            }
        }
    }

    // helper function in updateEvaluation
    private void updateThreatList(int lineIndex){
        for(int i = 0; i < threatList.length; i++){
            threatList[i].combine(myLines[lineIndex].getThreatMap(i));
        }
    }

    private void clearThreatList(){
        for(LocationList threatMap: threatList){
            threatMap.clear();
        }
    }
}
