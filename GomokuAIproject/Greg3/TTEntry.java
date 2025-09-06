package GomokuAIproject.Greg3;

import java.util.Arrays;

public class TTEntry {
    private int eval;
    private int depth;
    private int type;
    private int bestMoveFound;
    private int generation;
    private long hash;

    // type constants
    public static final int EXACT = 0;
    public static final int UPPER_BOUND = 1;
    public static final int LOWER_BOUND = 2;

    public static final int NULL = -1;

    public TTEntry(int eval, int depth, int type, int bestMoveFound, int generation, long hash){
        this.eval = eval;
        this.depth = depth;
        this.type = type;
        this.bestMoveFound = bestMoveFound;
        this.generation = generation;
        this.hash = hash;
    }

    public TTEntry(){
        eval = NULL;
        depth = NULL;
        type = NULL;
        bestMoveFound = NULL;
        generation = NULL;
        hash = NULL;
    }

    public TTEntry(TTEntry toCopy){
        this.eval = toCopy.getEval();
        this.depth = toCopy.getDepth();
        this.type = toCopy.getType();
        this.bestMoveFound = toCopy.getBestMove();
        this.generation = toCopy.getGeneration();
        this.hash = toCopy.getHash();
    }

    // sort of jank implementation but
    public boolean isNull(){
        return (eval == NULL);
    }

    public int getEval(){
        return eval;
    }

    public int getDepth(){
        return depth;
    }

    public int getType(){
        return type;
    }

    public int getBestMove(){
        return bestMoveFound;
    }

    public int getGeneration(){
        return generation;
    }

    public long getHash(){
        return hash;
    }

    // efficient way to reset TTEntry instead of creating new object
    public void set(int eval, int depth, int type, int bestMoveFound, int generation, long hash){
        this.eval = eval;
        this.depth = depth;
        this.type = type;
        this.bestMoveFound = bestMoveFound;
        this.generation = generation;
        this.hash = hash;
    }

}
