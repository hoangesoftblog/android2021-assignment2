package com.example.map;
import java.util.ArrayList; import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NoticeDialogListener, GoogleMap.OnInfoWindowClickListener {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final long UPDATE_INTERVAL = 20*1000 ;
    private static final long FASTEST_INTERVAL = 10*1000 ;
    protected FusedLocationProviderClient client;
    protected LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private List<Marker> markerList = new ArrayList<>();


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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(MapsActivity.this, AddLocationActivity.class);
                    intent.putExtra("latitude", latLng.latitude);
                    intent.putExtra("longitude", latLng.longitude);
                    intent.putExtra("owner", user.getUid());
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
//                String name = marker.getTitle();
//                Toast.makeText(MapsActivity.this, "Clicked location is " + name, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(this);
    }


    @Override
    protected void onResume(){
        super.onResume();
//        new GetLocation().execute();

        if (mMap != null) {
            Handler handler = new Handler();
            handler.postDelayed(this::refershSites, 500);
        }
    }

    public void clearOldMarkers() {
        mMap.clear();
        markerList.clear();
    }

    public void refershSites() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("location").get()
                .addOnSuccessListener((QuerySnapshot snapshots) -> {
                    List<DocumentSnapshot> documentSnapshots = snapshots.getDocuments();

                    // Todo: Change and customize (using Spannable) the
                    //  display snippet for owner, joined or not joined.
                    String userID = FirebaseAuth.getInstance().getUid();
                    for (DocumentSnapshot document: documentSnapshots) {
                        boolean isOwner = document.getString("owner").equals(userID);

                        MarkerOptions options = new MarkerOptions()
                                .position(new LatLng(
                                        document.getDouble("latitude"),
                                        document.getDouble("longitude")
                                ))
                                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.medical))
                                .title(document.getString("name"))
                                .snippet(isOwner ? "You are already the owner!" : "Click to join now");

                        Marker marker = mMap.addMarker(options);
                        marker.setTag(document.toObject(com.example.map.Location.class));
                        markerList.add(marker);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Get locations", "failed");
                    }
                });
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


    public BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth()
                , vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
//        // Test code
//        Toast.makeText(MapsActivity.this, "Random Football", Toast.LENGTH_SHORT).show();

        // Todo: Block the add when user already joins the place
        com.example.map.Location location = (com.example.map.Location) marker.getTag();
        String userID = FirebaseAuth.getInstance().getUid();

        if (!location.owner.equals(userID)) {
            DialogFragment confirmDialog = new MyDialog(location);
            confirmDialog.show(getSupportFragmentManager(), "Confirm joining the location");
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof MyDialog) {
            MyDialog myDialog = (MyDialog) dialog;
            com.example.map.Location location = myDialog.location;

            currentUserJoinLocation(location);
        }
        else {
            Toast.makeText(MapsActivity.this, "There is something wrong, dialog is not instanceof MyDialog", Toast.LENGTH_SHORT).show();
        }


//        // Test code
//        Toast.makeText(MapsActivity.this, "Random Football", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    public void currentUserJoinLocation(com.example.map.Location location) {
        String userID = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // WriteBatch is a way to execute a transaction in FirebaseFirestore
        WriteBatch batch = db.batch();

        // Update the current userID into location.memberIDs and update the location into users.locationsJoined
        DocumentReference locationRef = db.collection(DatabasePath.LOCATION).document(location.getName());
        batch.update(locationRef, "memberIDs", FieldValue.arrayUnion(userID));

        DocumentReference userRef = db.collection(DatabasePath.USER).document(userID);
        batch.update(userRef, "locationsJoined", FieldValue.arrayUnion(location.getName()));

        batch.commit()
                .addOnSuccessListener((Void unused) -> {
                    Toast.makeText(MapsActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();
                })
        .addOnFailureListener((@NonNull Exception e) -> {
            Toast.makeText(MapsActivity.this, "Failure, because " + e.getMessage(), Toast.LENGTH_LONG).show();
        })
        ;
    }


//    private class GetLocation extends AsyncTask<Void,Void,Void> {
//
//        String jsonString ="";
//        @Override
//        protected Void doInBackground(Void... voids) {
//            jsonString = HttpHandler.getRequest(getResources().getString(R.string.LOCATION_URL));
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid){
//            super.onPostExecute(aVoid);
//
//            try {
//                JSONArray jsonArray = new JSONArray(jsonString);
//
//                List<JSONObject> listObjects = new ArrayList<>();
//                for (int i=0; i < jsonArray.length();i++){
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    listObjects.add(jsonObject);
//                }
//
//                for (JSONObject jsonObject : listObjects) {
//                    LatLng position = new LatLng(
//                            jsonObject.getDouble("latitude"),
//                            jsonObject.getDouble("longitude"));
//
//                    String name = jsonObject.getString("name");
//
//                    mMap.addMarker(new MarkerOptions()
//                            .position(position)
//                            .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.medical))
//                            .snippet(jsonObject.getString("name")));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static class MyDialog extends DialogFragment {
        public com.example.map.Location location;

        public MyDialog(com.example.map.Location location) {
            super();
            this.location = location;
        }

        NoticeDialogListener listener;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Join the location");
            builder.setMessage("Do you want to join in \"" + location.name + "\"?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            listener.onDialogPositiveClick(MyDialog.this);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    })
            ;
            builder.setCancelable(true);

            return builder.create();
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);

            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                listener = (NoticeDialogListener) context;
            }
            catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(getActivity().toString()
                        + " must implement NoticeDialogListener");
            }
        }
    }
}

interface NoticeDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
}