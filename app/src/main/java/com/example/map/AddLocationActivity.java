package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;


public class AddLocationActivity extends AppCompatActivity {

    private Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        location = new Location();
        Intent intent = getIntent();
        location.latitude = intent.getDoubleExtra("latitude", 0);
        location.longitude = intent.getDoubleExtra("longitude", 0);
        String ownerTemp = intent.getStringExtra("owner");
        location.owner = (ownerTemp != null) ? ownerTemp : "Ghost";

        EditText resLat = findViewById(R.id.resLat);
        resLat.setText(location.latitude + "");
        EditText resLong = findViewById(R.id.resLong);
        resLong.setText(location.longitude + "");
    }

    public void onConfirmAddRestaurant(View view) {
        EditText resName = findViewById(R.id.resName);
        location.name = resName.getText().toString();

//        new PostRestaurant().execute();
        addLocation(location);
    }

    public void addLocation(Location location) {
        String ownerID = location.owner;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        // Add new location, and update location into user.locationOwned
        DocumentReference locationRef =  db.collection(DatabasePath.LOCATION).document(location.name);
        batch.set(locationRef, location);

        DocumentReference userRef = db.collection(DatabasePath.USER).document(ownerID);
        batch.update(userRef, "locationsOwned", FieldValue.arrayUnion(location.getName()));

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddLocationActivity.this, "Location created successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AddLocationActivity.this, MapsActivity.class);
                        setResult(101, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddLocationActivity.this, "Location created failed", Toast.LENGTH_LONG).show();
                    }
                })
        ;
    }

//    private class PostRestaurant extends AsyncTask<Void,Void,Void> {
//
//        private String status = "";
//        @Override
//        protected Void doInBackground(Void... voids) {
//            status = HttpHandler.postRequest(getResources().getString(R.string.LOCATION_URL), location);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Toast.makeText(AddLocationActivity.this,
//                    status, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(AddLocationActivity.this,
//                    MapsActivity.class);
//            setResult(101, intent);
//            finish();
//        }
//    }
}