package GomokuAIproject;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import GomokuAIproject.Constants.BoardConstants;
import GomokuAIproject.EngineHelpers.SuperMove;
import GomokuAIproject.Greg2.G2VirtualBoard;
import GomokuAIproject.Greg2.Greg2;
import GomokuAIproject.Greg3.G3VirtualBoard;

public class Manager {

    private static Board board;
    private static Engine engineRunning;
    private static GameMode gameMode;
    private static boolean isPlayerBlack;
    private static enum GameMode {
        PLAYER_VS_ENGINE,
        ENGINE_VS_ENGINE
    }

    // repeatedly alternates between player and engine turns
    public static void runPlayerVEngine(Engine engine, boolean isPlayerBlack){

        gameMode = GameMode.PLAYER_VS_ENGINE;
        engineRunning = engine;
        board = Board.getInstance();
        Manager.isPlayerBlack = isPlayerBlack;
        G3VirtualBoard g3Board = new G3VirtualBoard(isPlayerBlack);

        Thread gameLoop = new Thread(() ->{

            while(!board.isGameOver()){
                boolean isPlayerTurn = board.isBlackTurn() == isPlayerBlack;
                if(!isPlayerTurn){
                    board.placeStone(engine.playFromPosition(board));
                }
                try {
                    Thread.sleep(100);
                } catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                }
                // g3Board.sync();
                // g3Board.updateEvaluation(board.getLastMove());
                // System.out.println(g3Board.debugPrint(true));
                // System.out.println(g3Board.debugPrint(false));
                //System.out.println("G3 Eval: " + g3Board.getEvaluation()[0][0] / 10);
                // System.out.println("-------------");
                // g3Board.placeStone(board.getLastMove());
                // System.out.println("G3 Eval: " + g3Board.updateEvaluation(board.getLastMove())[0][0] / 10);
                // System.out.println("G2 Eval: " + new G2VirtualBoard(isPlayerBlack).getEvaluation()[0][0]);
                // System.out.println("----------");

                //System.out.println("Eval: " + new G3VirtualBoard(isPlayerBlack).getEvaluation()[0][0]);
                //  SuperMove depth5Best = new Greg2(isPlayerBlack, 5)
                //      .minimax(5, 5, isPlayerBlack, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

                // SuperMove depth4Best = new Greg2(!isPlayerBlack, 4, 20)
                //     .minimax(4, 4, !isPlayerBlack, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

                // SuperMove depth3Best = new Greg2(isPlayerBlack, 3, 20)
                //     .minimax(3, 3, isPlayerBlack, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

                // SuperMove depth2Best = new Greg2(!isPlayerBlack, 2, 20)
                //     .minimax(2, 2, !isPlayerBlack, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

                // SuperMove depth1Best = new Greg2(isPlayerBlack, 1, 20)
                //     .minimax(1, 1, isPlayerBlack, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

                // System.out.println("Evaluation: " + depth5Best.getScore() + " Move: " + depth5Best.getMoveLocation());
                // System.out.println("Evaluation: " + depth4Best.getScore() + " Move: " + depth4Best.getMoveLocation());
                // System.out.println("Evaluation: " + depth3Best.getScore() + " Move: " + depth3Best.getMoveLocation());
                // System.out.println("Evaluation: " + depth2Best.getScore() + " Move: " + depth2Best.getMoveLocation());
                // System.out.println("Evaluation: " + depth1Best.getScore() + " Move: " + depth1Best.getMoveLocation());
            }

        });

        gameLoop.start();
    }


    // engine1 starts as black, engine2 as white
    public static void runEngineVEngine(Engine engine1, Engine engine2, int numberOfRounds, boolean randomStart){
        gameMode = GameMode.ENGINE_VS_ENGINE;
        board = Board.getInstance();

        Thread gameLoop = new Thread(() -> {
            int engine1Wins = 0;
            int engine2Wins = 0;
            int ties = 0;
            ArrayList<Double> engine1Times = new ArrayList<Double>();
            ArrayList<Double> engine2Times = new ArrayList<Double>();
            ArrayList<Integer> centerSquares = new ArrayList<Integer>();

            double timeRatiosTotal = 1;
            double timeRatios = 1;

            for(int round = 0; round < numberOfRounds; round++){

                for(int match = 0; match < 2; match++){ // two matches for every round

                    Board.reset();  // initialize new game
                    if(match % 2 == 0){
                        engine1.setIsOpponentBlack(false);
                        engine2.setIsOpponentBlack(true);
                    } else {
                        engine1.setIsOpponentBlack(true);
                        engine2.setIsOpponentBlack(false);
                    }

                    if(randomStart){ // place 3 random stones in the center
                        if(match == 0){ // generate new opening position if start of new round. Otherwise, use last opening so each engine gets each color
                            centerSquares = new ArrayList<Integer>();
                            for(int row = 0; row < 5; row++){
                                int rowStart = 56 + row * 13;
                                for(int col = 0; col < 5; col++){
                                    centerSquares.add(rowStart + col);
                                }
                            }
                            while(centerSquares.size() > 3){
                                centerSquares.remove((int)(Math.random() * centerSquares.size()));
                            }
                        }
                        for(Integer square: centerSquares){
                            board.placeStone(square);
                        }
                    }

                    double engine1Time = 0;
                    double engine2Time = 0;
                    while(!board.isGameOver()){
                        boolean isEngine1Turn = board.isBlackTurn() == engine2.getIsOpponentBlack();
                        if(isEngine1Turn){
                            Instant startTime = Instant.now();
                            board.placeStone(engine1.playFromPosition(board));
                            Instant endTime = Instant.now();
                            engine1Time = Duration.between(startTime, endTime).toMillis()/1000.0;
                        }else{
                            Instant startTime = Instant.now();
                            board.placeStone(engine2.playFromPosition(board));
                            Instant endTime = Instant.now();
                            engine2Time = Duration.between(startTime, endTime).toMillis()/1000.0;
                        }
                        if(engine1Time != 0 && engine2Time != 0){
                            engine1Times.add(engine1Time);
                            engine2Times.add(engine2Time);
                            // timeRatiosTotal *= (engine2Time / engine1Time); // might need to switch if too many rounds (+=)
                            // timeRatios++;
                            engine1Time = 0;
                            engine2Time = 0;
                        }
                    }

                    String outcome = "E1";
                    if(board.getGameState() == BoardConstants.BLACK_WINS){
                        if(engine2.getIsOpponentBlack()){
                            engine1Wins++;
                            outcome += "W";
                        }else{
                            engine2Wins++;
                            outcome += "L";
                        }
                    }else if(board.getGameState() == BoardConstants.WHITE_WINS){
                        if(engine2.getIsOpponentBlack()){
                            engine2Wins++;
                            outcome += "L";
                        }else{
                            engine1Wins++;
                            outcome += "W";
                        }
                    }else if(board.getGameState() == BoardConstants.TIE){
                        ties++;
                        outcome += "T";
                    }
    
                    System.out.println("Match: " + (round * 2 + match) + ", Outcome: " + outcome + ", Export: " + Board.exportGame());
                }
                System.out.println();
            }

            System.out.println("Engine 1 Wins: " + engine1Wins + ", Engine 2 Wins: " + engine2Wins);
            System.out.println("And " + ties + " draws");
            double engine1TotalTime = 0;
            for(int i = 0; i < engine1Times.size(); i++){
                engine1TotalTime += engine1Times.get(i);
            }
            double engine2TotalTime = 0;
            for(int i = 0; i < engine2Times.size(); i++){
                engine2TotalTime += engine2Times.get(i);
            }
            //System.out.println("Engine1TotalTime: " + engine1TotalTime + " Engine2TotalTime: " + engine2TotalTime);
            System.out.println("And Engine 1 is about " + Math.round(engine2TotalTime/engine1TotalTime*1000.0)/1000.0 + "x faster than Engine 2");
            // System.out.print("Engine 1 Times: ");
            // for(int i = 0; i < engine1Times.size(); i++){
            //     System.out.print(engine1Times.get(i) + " ");
            // }
            // System.out.print("Engine 2 Times: ");
            // for(int i = 0; i < engine2Times.size(); i++){
            //     System.out.print(engine2Times.get(i) + " ");
            // }
        });

        gameLoop.start();
    }

    public static boolean canPlayerMove(){
        boolean isPlayerTurn = board.isBlackTurn() == isPlayerBlack;
        if(gameMode == GameMode.PLAYER_VS_ENGINE && isPlayerTurn){
            return true;
        }
        return false;
    }

}
