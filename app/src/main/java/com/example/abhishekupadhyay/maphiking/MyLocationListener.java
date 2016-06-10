package com.example.abhishekupadhyay.maphiking;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by abhishekupadhyay on 09/06/16.
 */
public class MyLocationListener implements LocationListener {

    private Context context;
    private ILocationChange iLocationChange;

    public MyLocationListener(Context context, ILocationChange iLocationChange) {
        this.context = context;
        this.iLocationChange = iLocationChange;
    }

    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(context,
                "Location changed: Lat: " + location.getLatitude() + " Lng: "
                        + location.getLongitude(), Toast.LENGTH_SHORT).show();

        iLocationChange.onLocationChange(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
