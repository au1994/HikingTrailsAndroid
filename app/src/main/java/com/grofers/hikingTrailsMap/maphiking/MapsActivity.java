package com.grofers.hikingTrailsMap.maphiking;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ILocationChange, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Location currentLocation;
    private String TAG  = MapsActivity.class.getSimpleName();
    private List<LatLng> trail = new ArrayList<>();
    private Context context;
    private TextView startHiking, stopHiking;
    private String androidId="";

    private static final int MY_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = MapsActivity.this;

        androidId = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        Log.i(TAG, "your android id " + androidId);

        //Todo: check for saved instance

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startHiking = (TextView) findViewById(R.id.startHiking);
        startHiking.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.startHiking:
                if(startHiking.getText().toString().equals("START HIKING"))
                {
                    //Todo: start hiking:-
                    Log.i(TAG, "start button clicked");

                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        // TODO: Consider calling
                        // ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                        int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSION
                        );

                    }
                    else
                    {
                        startHiking.setText(R.string.stop_hiking);
                        try
                        {
                            Log.i(TAG, "fix is here");
                            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            LocationListener locationListener = new MyLocationListener(getBaseContext(), MapsActivity.this);
                            currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER, 10000, 10, locationListener);

                            // Add a marker of your current location and move the camera
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            trail.add(latLng);
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                        }
                        catch (SecurityException | NullPointerException e)
                        {
                            Log.i(TAG, "onMapReady");
                            e.printStackTrace();
                        }

                    }
                }
                else
                {
                    new createTrailAsyncTask().execute();
                    startHiking.setText(R.string.start_hiking);
                }
                break;


            default:
                break;

        }


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

        LatLng latLng = new LatLng(28.459497, 77.026638);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

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
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.

                    try
                    {
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        LocationListener locationListener = new MyLocationListener(getBaseContext(), MapsActivity.this);
                        currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, 10000, 10, locationListener);

                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        trail.add(latLng);
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));




                    }catch (SecurityException | NullPointerException e)
                    {
                        e.printStackTrace();
                    }



                }
                else
                {
                    //Todo: toast can't do without gps??
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChange(Location location) {
        //Todo plot on map here
        Log.i(TAG, "MapsActivity onLocationChange called");
        System.out.println("changing frequently");
        //Todo: add location to trail(arraylist<LatLng>)
        List<LatLng> points  = new ArrayList<LatLng>();
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        LatLng newLatLng = new LatLng(location.getLatitude(),location.getLongitude());
        currentLocation = location;
        trail.add(newLatLng);
        points.add(currentLatLng);
        points.add(newLatLng);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(points).color(Color.BLUE).width(5).geodesic(true);
        mMap.addPolyline(polylineOptions);


    }


    //Async Tasks
    private class createTrailAsyncTask extends AsyncTask<Void, Void, String>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Creating Your Trail, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", androidId);
                jsonObject.put("trail", trail);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = "http://127.0.0.1:5000/trails/create";
            String response = "";
            PostJsonData postJsonData = new PostJsonData(jsonObject, url);

            try {
                response = postJsonData.postData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("response " + response);

            return response;
        }

        @Override
        protected void onPostExecute(String jsonObject)
        {
            super.onPostExecute(jsonObject);
            progressDialog.hide();

        }
    }
}
