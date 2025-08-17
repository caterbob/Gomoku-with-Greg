package GomokuAIproject.Greg3;

import java.util.Arrays;

public class LocationList {
    private boolean[] containsLocation;
    private int[] locations;
    private int size;

    public LocationList(){
        containsLocation = new boolean[169];
        locations = new int[169];
        Arrays.fill(locations, -1);
        size = 0;
    }

    public void addLocation(int location){
        if(containsLocation[location]){
            return; // invalid or already added
        }
        containsLocation[location] = true;
        locations[size++] = location;
    }

    public int getLocation(int index){
        return locations[index];
    }

    public int getSize(){
        return size;
    }

    public void clear(){
        Arrays.fill(containsLocation, false);
        Arrays.fill(locations, -1);
        size = 0;
    }

    public boolean containsLocation(int location){
        return containsLocation[location];
    }

    // combines this location list with another
    public void combine(LocationList other){
        for(int i = 0; i < other.getSize(); i++){
            addLocation(other.getLocation(i));
        }
    }

    public boolean hasOverlap(LocationList other){
        for(int i = 0; i < size; i++){
            if(other.containsLocation(locations[i]))
                return true;
        }
        return false;
    }

}
