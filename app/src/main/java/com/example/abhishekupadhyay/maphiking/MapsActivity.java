package com.example.abhishekupadhyay.maphiking;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ILocationChange, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private Location currentLocation;
    private static final int MY_PERMISSION = 123;
    private String TAG  = "MapsActivity Log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        String androidID = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        Log.i(TAG, "your android id " + androidID);

        //Todo: check for saved instance
        //Todo: put all the strings in string.xml

        Button startHiking = (Button)

        /*
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("please turn gps on")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        */



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;



            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION
                );

            } else {
                try
                {
                    Log.i(TAG, "fix is here");
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    LocationListener locationListener = new MyLocationListener(getBaseContext(), MapsActivity.this);
                    currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 2000, 5, locationListener);

                    // Add a marker of your current location and move the camera
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                } catch (SecurityException | NullPointerException e) {
                    Log.i(TAG, "onMapReady");
                    e.printStackTrace();
                }

            }









    }


    //getters and setters
    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.

                    try
                    {
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        LocationListener locationListener = new MyLocationListener(getBaseContext(), MapsActivity.this);
                        currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, 2000, 5, locationListener);

                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));




                    }catch (SecurityException | NullPointerException e)
                    {
                        e.printStackTrace();
                    }



                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChange(Location location) {
        //Todo plot on map here
        Log.i(TAG, "changing frequently");
        System.out.println("changing frequently");
        ArrayList<LatLng> points  = new ArrayList<LatLng>();
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        LatLng newLatLng = new LatLng(location.getLatitude(),location.getLongitude());
        currentLocation = location;
        points.add(currentLatLng);
        points.add(newLatLng);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(points).color(Color.BLUE).width(5).geodesic(true);
        mMap.addPolyline(polylineOptions);


    }
}
