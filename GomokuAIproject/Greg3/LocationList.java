package GomokuAIproject.Greg3;

import java.util.Arrays;

public class LocationList {
    private int[] locationInstances;
    private int[] locations;
    private int size;
    public int[] scratch;   // used as temp int[] for locations

    public LocationList(){
        locationInstances = new int[169];
        locations = new int[169];
        scratch = new int[169];
        Arrays.fill(locationInstances, 0);
        Arrays.fill(locations, -1);
        Arrays.fill(scratch, -1);
        size = 0;
    }

    public void addLocation(int location){
        if(locationInstances[location] == 0){
            locations[size++] = location;
        }
        locationInstances[location]++;
        //System.out.println(locationInstances[location]);
    }

    public int getLocation(int index){
        return locations[index];
    }

    public int getSize(){
        return size;
    }

    public void clear(){
        Arrays.fill(locationInstances, 0);
        Arrays.fill(locations, -1);
        size = 0;
    }

    public boolean containsLocation(int location){
        return locationInstances[location] > 0;
    }

    // combines this location list with another
    public void combine(LocationList other){
        for(int i = 0; i < other.getSize(); i++){
            addLocation(other.getLocation(i));
        }
    }

    // locations must be compacted later!
    public void subtract(LocationList other){
        for(int i = 0; i < other.getSize(); i++){
            locationInstances[other.getLocation(i)]--;
        }
        compactLocations();
    }

    // remove locations that no longer exist. Helper fucntion in subtract()
    private void compactLocations(){
        int scratchIndex = 0;
        Arrays.fill(scratch, -1);
        for(int i = 0; i < size; i++){
            if(locationInstances[locations[i]] > 0)
                scratch[scratchIndex++] = locations[i];
        }
        System.arraycopy(scratch, 0, locations, 0, scratch.length);
        size = scratchIndex;
    }

    public boolean hasOverlap(LocationList other){
        for(int i = 0; i < size; i++){
            if(other.containsLocation(locations[i]))
                return true;
        }
        return false;
    }

}
