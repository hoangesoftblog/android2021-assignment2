package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rcvLocation;
    private LocationListAdapter mLocationAdapter;
    private List<Location> mListLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initUi();

        BottomNavigationView navigationView =findViewById(R.id.navbar);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_user:;
                        Toast.makeText(ListActivity.this, "User", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ListActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_location:;
                        Toast.makeText(ListActivity.this, "Location", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(ListActivity.this, MapsActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_list:;
                        Toast.makeText(ListActivity.this, "List", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(ListActivity.this, ListActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

    }
    private void initUi(){
        rcvLocation = findViewById(R.id.rcv_users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLocation.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvLocation.addItemDecoration(dividerItemDecoration);

        mLocationAdapter = new LocationListAdapter(mListLocations);

        rcvLocation.setAdapter(mLocationAdapter);
        getListLocation();
    }

    private void getListLocation(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("location").get()
                .addOnSuccessListener((QuerySnapshot snapshots) -> {
                    List<DocumentSnapshot> documentSnapshots = snapshots.getDocuments();

                    for (DocumentSnapshot document: documentSnapshots) {
                        Location loc = document.toObject(Location.class);
                        if (loc != null) mListLocations.add(loc);
                    }

                    mLocationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Get locations", "failed");
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
          startActivity(new Intent(ListActivity.this, LoginActivity.class));

        }
    }
}