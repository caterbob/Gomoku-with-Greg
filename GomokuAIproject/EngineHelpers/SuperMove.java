package GomokuAIproject.EngineHelpers;

public class SuperMove{

    private int moveLocation;
    private double score;

    public SuperMove(int moveLocation, double score){
        this.moveLocation = moveLocation;
        this.score = score;
    }

    public SuperMove(int moveLocation, SuperMove PassInMove){
        if(PassInMove.getMoveLocation() == -1){
            this.moveLocation = moveLocation;
            this.score = PassInMove.getScore();
        } else {
            this.moveLocation = PassInMove.getMoveLocation();
            this.score = PassInMove.getScore();
        }
    }

    public int getMoveLocation(){
        return moveLocation;
    }

    public double getScore(){
        return score;
    }

    // used to avoid creating more objects than necessary
    public void set(int location, double score){
        this.moveLocation = location;
        this.score = score;
    }

}