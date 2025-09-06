package GomokuAIproject.Greg3;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class TranspositionTable{

    private TTEntry[] table;
    private int maximumSize;
    private static int AGE_THRESHOLD = 2;
    private boolean fix;

    public TranspositionTable(int maximumSize, boolean fix){
        table = new TTEntry[maximumSize];
        this.maximumSize = maximumSize;
        this.fix = fix;
    }

	public void add(long hash, TTEntry entry){
        int index = (int)((hash & 0x7FFFFFFFFFFFFFFFL) % maximumSize);
        TTEntry oldEntry = table[index];
        if( oldEntry.isNull() || 
            entry.getDepth() >= oldEntry.getDepth() 
            || entry.getGeneration() - oldEntry.getGeneration() > AGE_THRESHOLD
        ){
            table[index] = entry;   // replace oldEntry
        }
    }

    public void add(long hash, int eval, int depth, int type, int bestMoveFound, int generation){
        int index = (int)((hash & 0x7FFFFFFFFFFFFFFFL) % maximumSize);
        TTEntry oldEntry = table[index];
        if( oldEntry.isNull() || 
            depth >= oldEntry.getDepth() 
            || generation - oldEntry.getGeneration() > AGE_THRESHOLD
        ){
            // if(fix){
                table[index].set(eval, depth, type, bestMoveFound, generation, hash);   // replace oldEntry
            // }else{
            //     table[index] = new TTEntry(eval, depth, type, bestMoveFound, generation, hash);   // replace oldEntry
            // }
        }
    }

    public TTEntry get(long hash){
        int index = (int)((hash & 0x7FFFFFFFFFFFFFFFL) % maximumSize);
        if(table[index] != null && table[index].getHash() == hash)
            return table[index];
        return null;
    }

     public void clear(){
        Arrays.fill(table, null);
        initializeTable();
    }

    // ran to pre-fill entire array with TTEntries for faster speed
    public void initializeTable(){
        for(int i = 0; i < maximumSize; i++){
            table[i] = new TTEntry();
        }
    }
}
