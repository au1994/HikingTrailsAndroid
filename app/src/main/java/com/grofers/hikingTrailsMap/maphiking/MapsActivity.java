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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ILocationChange, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    private GoogleMap mMap;

    private Context context;

    private Location currentLocation;

    private ProgressDialog progressDialog;

    private PlaceAutocompleteFragment autocompleteFragment;

    private CardView cardView;

    private String TAG  = MapsActivity.class.getSimpleName();
    private String androidId="";

    private List<LatLng> trail = new ArrayList<>();

    private TextView startHiking;

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
        startHiking.setTag("start");
        startHiking.setOnClickListener(this);

        cardView = (CardView) findViewById(R.id.card_view);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                startHiking.setVisibility(View.GONE);
                searchTrail(place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }




    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }*/

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

                    int permissionCheck = ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION);

                    int granted = PackageManager.PERMISSION_GRANTED;


                    Log.d("permission check", String.valueOf(permissionCheck));


                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSION
                        );

                    }
                    else
                    {

                        startHiking.setTag("stop");
                        cardView.setVisibility(View.GONE);
                        startHiking.setText(R.string.stop_hiking);
                        try
                        {
                            Log.i(TAG, "fix is here");
                            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            LocationListener locationListener = new MyLocationListener(getBaseContext(), MapsActivity.this);
                            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

                            // Add a marker of your current location and move the camera
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            trail.add(latLng);
                            mMap.clear();
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
                        cardView.setVisibility(View.VISIBLE);
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

                    startHiking.setTag("stop");
                    cardView.setVisibility(View.GONE);
                    startHiking.setText(R.string.stop_hiking);

                    try
                    {
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        LocationListener locationListener = new MyLocationListener(getBaseContext(), MapsActivity.this);
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        trail.add(latLng);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location"));
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

        try
        {
            //Todo plot on map here
            Log.i(TAG, "MapsActivity onLocationChange called");
            System.out.println("changing frequently");
            //Todo: add location to trail(arraylist<LatLng>)
            List<LatLng> points  = new ArrayList<>();
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
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

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
        List<Double> loc = new ArrayList<>();
        loc.add(trail.get(0).latitude);
        loc.add(trail.get(0).longitude);
        StartingPoint startingPoint = new StartingPoint("Point", loc);
        TrailDetail trailDetail = new TrailDetail(trail, androidId, startingPoint);
        //StringBuffer buffer = new StringBuffer("http://192.168.1.55:5000/trails/create");
        String awsIp = getResources().getString(R.string.AWSIp);
        String localIp = getResources().getString(R.string.localIp);
        String url = "http://" + awsIp +":5000/trails/create";
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Response: ", response.toString());
                progressDialog.hide();
                //Todo: share trail must not be here


            }
        });

        postJsonData.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(postJsonData);
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
        trail.clear();
        mMap.clear();
        shareUrl = shareUrl + params;
        Intent shareIntent  = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My New Trail");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        startActivity(shareIntent);
    }

    private void searchTrail(Double lat, Double lon)
    {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Getting Nearby Trails, Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        //StringBuffer buffer = new StringBuffer("http://192.168.1.55:5000/trails/search?lat=\" + lat + \"&lon=\" + lon");
        //buffer.append("lat");

        String awsIp = getResources().getString(R.string.AWSIp);
        String localIp = getResources().getString(R.string.localIp);
        String url = "http://" + awsIp + ":5000/trails/search?lat=" + lat + "&lon=" + lon;
        Log.d("search url", url);

        GetNearbyTrails getNearbyTrails = new GetNearbyTrails(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response: ", response.toString());
                progressDialog.hide();
                if(response.length() == 0)
                {
                    CharSequence message = "No Results Found";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, message, duration);
                    toast.show();
                }
                addMarkers(response);
                //plotRoute();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Response: ", response.toString());
                progressDialog.hide();
                //Todo: toast error getting trails

            }
        });

        getNearbyTrails.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(this).addToRequestQueue(getNearbyTrails);

    }

    private void addMarkers(JSONArray response)
    {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TrailDetail>>(){}.getType();
        List<TrailDetail> trailList = gson.fromJson(response.toString(), listType);
        /*for(int i = 0 ; i < trailList.size(); i++)
        {
                //createMarker(trailList.get(i).getLoc().get(0), trailList.get(i).getLoc().get(1));
            System.out.println("lat "+trailList.get(i).getLoc().get(0));
        }*/
        mMap.clear();
        /*for(int i=0 ; i< response.length() ; i++)
        {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                JSONArray jsonArray = jsonObject.getJSONObject("startingPoint").getJSONArray("coordinates");
                createMarker(( jsonArray.getDouble(0)), (jsonArray.getDouble(1)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
        for(int i = 0 ; i< trailList.size() ;i++)
        {
            plotRoute(trailList.get(i).getTrail());
        }

        startHiking.setVisibility(View.VISIBLE);

    }

    private void createMarker(Double lat, Double lon)
    {

        LatLng latLng = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(latLng).title("NearBy Grofers"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));

    }

    private void plotRoute(List<LatLng> trail)
    {
        Log.d(TAG, trail.toString());
        LatLng latLng = trail.get(0);
        PolylineOptions polylineOptions = new PolylineOptions().color(Color.BLUE).width(5);
        polylineOptions.addAll(trail);
        mMap.addPolyline(polylineOptions);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Trail"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }

}
