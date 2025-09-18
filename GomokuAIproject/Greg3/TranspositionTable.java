package GomokuAIproject.Greg3;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class TranspositionTable{

    private TTEntry[] table;
    private int maximumSize;
    private static int AGE_THRESHOLD = 2;

    public TranspositionTable(int maximumSize){
        table = new TTEntry[maximumSize];
        this.maximumSize = maximumSize;
        Arrays.fill(table, new TTEntry());
    }

	public void add(long hash, TTEntry entry){
        int index = (int)((hash & 0x7FFFFFFFFFFFFFFFL) % maximumSize);
        TTEntry oldEntry = table[index];
        if( oldEntry.isEmpty() || 
            entry.getDepth() >= oldEntry.getDepth() 
            || entry.getGeneration() - oldEntry.getGeneration() > AGE_THRESHOLD
        ){
            oldEntry.set(entry.getEval(), entry.getDepth(), entry.getType(), entry.getBestMove()
            , entry.getGeneration(), entry.getHash());
        }
    }

    public void add(long hash, int eval, int depth, int type, int bestMove, int gen){
        int index = (int)((hash & 0x7FFFFFFFFFFFFFFFL) % maximumSize);
        TTEntry oldEntry = table[index];
        if( oldEntry.isEmpty() || 
            depth >= oldEntry.getDepth() 
            || gen - oldEntry.getGeneration() > AGE_THRESHOLD
        ){
            oldEntry.set(eval, depth, type, bestMove, gen, hash);
        }
    }

    public TTEntry get(long hash){
        int index = (int)((hash & 0x7FFFFFFFFFFFFFFFL) % maximumSize);
        if(!table[index].isEmpty() && table[index].getHash() == hash)
            return table[index];
        return null;
    }

     public void clear(){
        for(TTEntry entry: table){
            entry.set(Integer.MIN_VALUE, 0, 0, 0, 0, 0);
        }
    }
}
