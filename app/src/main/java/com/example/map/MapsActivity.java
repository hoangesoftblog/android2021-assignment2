package com.example.map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String Location_API_URL = "http://10.0.2.2:3000/sites";

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final long UPDATE_INTERVAL = 20*1000 ;
    private static final long FASTEST_INTERVAL = 10*1000 ;
    protected FusedLocationProviderClient client;
    protected LocationRequest mLocationRequest;
    private GoogleMap mMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView navigationView = findViewById(R.id.navbar);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_user:;
                        Toast.makeText(MapsActivity.this, "User", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_location:;
                        Toast.makeText(MapsActivity.this, "Location", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(MapsActivity.this, MapsActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_list:;
                        Toast.makeText(MapsActivity.this, "List", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(MapsActivity.this, ListActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent intent = new Intent(MapsActivity.this, AddLocationActivity.class);
                    intent.putExtra("latitude", latLng.latitude);
                    intent.putExtra("longitude", latLng.longitude);
                    startActivity(intent);
                }
                    else {
                        startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                    }
            }});

            
        startLocationUpdate();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String name = marker.getTitle();
                Toast.makeText(MapsActivity.this, "Clicked location is " + name, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
        new GetLocation().execute();
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    @SuppressLint("MissingPermission")
    public void getPosition(View view){
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                Toast.makeText(MapsActivity.this, "(" + location.getLatitude() + ","
                        + location.getLongitude() +")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void startLocationUpdate(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        client.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                Toast.makeText(MapsActivity.this, "(" + location.getLatitude() + ","
                        + location.getLongitude() +")", Toast.LENGTH_SHORT).show();
            }
        }, null);
    }

    private class GetLocation extends AsyncTask<Void,Void,Void> {
        String jsonString ="";
        @Override
        protected Void doInBackground(Void... voids) {
            jsonString = HttpHandler.getRequest(Location_API_URL);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);

            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i=0; i < jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    LatLng position = new LatLng(
                            jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"));
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.medical))
                            .snippet(jsonObject.getString("name")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth()
                , vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}