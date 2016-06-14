package com.grofers.hikingTrailsMap.maphiking;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishekupadhyay on 13/06/16.
 */
public class StartingPoint {
    String type;
    List<Double> coordinates = new ArrayList<>();

    public StartingPoint(String type, List<Double> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
}
