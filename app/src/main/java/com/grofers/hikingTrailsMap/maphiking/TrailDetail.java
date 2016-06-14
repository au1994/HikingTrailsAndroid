package com.grofers.hikingTrailsMap.maphiking;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishekupadhyay on 10/06/16.
 */
public class TrailDetail {

    private String userId;
    private String trailId;
    private List<LatLng> trail = new ArrayList<>();
    private StartingPoint startingPoint;

    public TrailDetail(String userId, String trailId, List<LatLng> trail, StartingPoint startingPoint) {
        this.userId = userId;
        this.trailId = trailId;
        this.trail = trail;
        this.startingPoint = startingPoint;
    }

    public TrailDetail(List<LatLng> trail, String userId, StartingPoint startingPoint) {
        this.trail = trail;
        this.userId = userId;
        this.startingPoint = startingPoint;
    }

    public StartingPoint getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(StartingPoint startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTrailId() {
        return trailId;
    }

    public void setTrailId(String trailId) {
        this.trailId = trailId;
    }

    public List<LatLng> getTrail() {
        return trail;
    }

    public void setTrail(List<LatLng> trail) {
        this.trail = trail;
    }
}
