package com.grofers.hikingTrailsMap.maphiking;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ILocationChange, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    private GoogleMap mMap;

    private Context context;

    private Location currentLocation;

    private String TAG  = MapsActivity.class.getSimpleName();
    private String androidId="";

    private List<LatLng> trail = new ArrayList<>();

    private TextView startHiking;

    private RequestQueue queue;

    private static final int MY_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = MapsActivity.this;

        queue = Volley.newRequestQueue(context);

        androidId = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        Log.i(TAG, "your android id " + androidId);

        //Todo: check for saved instance

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startHiking = (TextView) findViewById(R.id.startHiking);
        startHiking.setTag("start");
        startHiking.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.startHiking:
                if(startHiking.getTag().toString().equals("start"))
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
                        startHiking.setTag("stop");
                        startHiking.setText(R.string.stop_hiking);
                        try
                        {
                            Log.i(TAG, "fix is here");
                            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            LocationListener locationListener = new MyLocationListener(getBaseContext(), MapsActivity.this);
                            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
                    if(trail.size() <= 1)
                    {
                        CharSequence message = "You have not yet started hiking";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();
                    }
                    else
                    {
                        createTrail();
                        startHiking.setTag("start");
                        startHiking.setText(R.string.start_hiking);
                    }

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
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
    private void createTrail()
    {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Creating Your Trail, Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        Gson gson = new Gson();
        TrailDetail trailDetail = new TrailDetail(trail, androidId);
        String url = "http://192.168.1.55:5000/trails/create";
        String jsonString = gson.toJson(trailDetail);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PostJsonData postJsonData = new PostJsonData(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response: ", response.toString());
                progressDialog.hide();
                shareTrail();
                trail.clear();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Response: ", response.toString());
                progressDialog.hide();
                //Todo: share trail must not be here
                shareTrail();

            }
        });

        postJsonData.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postJsonData);

    }

    private void shareTrail()
    {
        //String shareUrl = "https://www.google.com/maps?saddr=San+Francisco&daddr=GooglePlex+Mountain+View+" +
        //        "to:Google+Building+45+to:San+Jose";
        String shareUrl = "https://www.google.com/maps?";
        String params = "";
        int size = trail.size();
        Log.d("trail length", String.valueOf(size));
        params = params + "saddr=" + trail.get(0).latitude + "," + trail.get(0).longitude;
        params = params + "&";
        params = params + "daddr=" + trail.get(size - 1).latitude + "," + trail.get(size - 1).longitude;
        for(int i = 1; i < size - 1; i++)
        {
            params = params + "+to:" + trail.get(i).latitude + "," + trail.get(i).longitude;
        }
        //String urlFormat = "http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345";
        shareUrl = shareUrl + params;
        Intent shareIntent  = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My New Trail");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        startActivity(shareIntent);
    }
}
