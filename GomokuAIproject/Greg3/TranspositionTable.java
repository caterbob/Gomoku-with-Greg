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
    }

	public void add(long hash, TTEntry entry){
        int index = (int)((hash & 0x7FFFFFFFFFFFFFFFL) % maximumSize);
        TTEntry oldEntry = table[index];
        if( oldEntry == null || 
            entry.getDepth() >= oldEntry.getDepth() 
            || entry.getGeneration() - oldEntry.getGeneration() > AGE_THRESHOLD
        ){
            table[index] = entry;   // replace oldEntry
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
    }
}
