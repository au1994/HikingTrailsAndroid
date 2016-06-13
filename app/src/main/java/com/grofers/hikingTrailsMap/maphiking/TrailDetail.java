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
    private List<Double> loc = new ArrayList<>();

    public TrailDetail(String userId, String trailId, List<LatLng> trail, List<Double> loc) {
        this.userId = userId;
        this.trailId = trailId;
        this.trail = trail;
        this.loc = loc;
    }

    public TrailDetail(List<LatLng> trail, String userId, List<Double> loc) {
        this.trail = trail;
        this.userId = userId;
        this.loc = loc;
    }

    public List<Double> getLoc() {
        return loc;
    }

    public void setLoc(List<Double> loc) {
        this.loc = loc;
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
