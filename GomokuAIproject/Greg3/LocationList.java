package GomokuAIproject.Greg3;

import java.util.Arrays;

public class LocationList {
    private int[] locationInstances;
    private int[] locations;
    private int size;
    private int start; // where to start in locations (needed because of buffer space)
    public int[] scratch;   // used as temp int[] for locations

    private static final int NON_EXISTENT = -2;
    private static final int ENTIRE_ARRAY_SIZE = 250;
    private static final int BUFFER_SIZE = ENTIRE_ARRAY_SIZE - 169;

    public LocationList(){
        locationInstances = new int[169];
        locations = new int[250];   // bigger than 169 to offer buffer space
        scratch = new int[250];
        Arrays.fill(locationInstances, 0);
        Arrays.fill(locations, -1);
        Arrays.fill(scratch, -1);
        start = BUFFER_SIZE;
        size = 0;
    }

    public void addLocation(int location){
        if(locationInstances[location] == 0){
            locations[start + size++] = location;
        }
        locationInstances[location]++;
        //System.out.println(locationInstances[location]);
    }

    public int getLocation(int index){
        return locations[start + index];
    }

    public int getSize(){
        return size;
    }

    public int getStart(){
        return start;
    }

    public void clear(){
        Arrays.fill(locationInstances, 0);
        Arrays.fill(locations, -1);
        start = BUFFER_SIZE;
        size = 0;
    }

    public boolean containsLocation(int location){
        return locationInstances[location] > 0;
    }

    // combines this location list with another
    public void combine(LocationList other){
        for(int i = 0; i < other.getSize(); i++){
            addLocation(other.getLocation(i));  // getLocation handles start
        }
    }

    // locations must be compacted later!
    public void subtract(LocationList other){
        for(int i = 0; i < other.getSize(); i++){
            locationInstances[other.getLocation(i)]--;  //getLocation handles start
        }
        removeGoneLocations();
    }

    // remove locations that no longer exist according to locationInstances
    //Helper function in subtract()
    private void removeGoneLocations(){
        int scratchIndex = BUFFER_SIZE;
        Arrays.fill(scratch, -1);
        for(int i = start; i < start + size; i++){
            if(locationInstances[locations[i]] > 0)
                scratch[scratchIndex++] = locations[i];
        }
        System.arraycopy(scratch, 0, locations, 0, scratch.length);
        size = scratchIndex - BUFFER_SIZE;
        start = BUFFER_SIZE;
    }

    // removes locations that have been moved in the array
    // Helper function for bringToFront
    private void compactLocations(){
        int scratchIndex = BUFFER_SIZE;
        Arrays.fill(scratch, -1);
        for(int i = 0; i < size + 1; i++){  // size + 1 to account for new NON_EXISTENT location
            if(getLocation(i) == NON_EXISTENT)
                continue;
            scratch[scratchIndex++] = getLocation(i);
        }
        System.arraycopy(scratch, 0, locations, 0, scratch.length);
        size = scratchIndex - BUFFER_SIZE;
        start = BUFFER_SIZE;
    }

    public void bringLocationToFront(int index){
        int locationConsidered = getLocation(index);
        locations[start + index] = NON_EXISTENT;
        start--;
        locations[start] = locationConsidered;
        compactLocations();
    }

    public boolean hasOverlap(LocationList other){
        for(int i = start; i < start + size; i++){
            if(other.containsLocation(locations[i]))
                return true;
        }
        return false;
    }
}
