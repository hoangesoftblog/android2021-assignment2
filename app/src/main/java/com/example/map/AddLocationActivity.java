package com.example.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


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

        EditText resLat = findViewById(R.id.resLat);
        resLat.setText(location.latitude + "");
        EditText resLong = findViewById(R.id.resLong);
        resLong.setText(location.longitude + "");

    }

    public void onConfirmAddRestaurant(View view) {
        EditText resName = findViewById(R.id.resName);
        location.name = resName.getText().toString();


        new PostRestaurant().execute();
    }

    private class PostRestaurant extends AsyncTask<Void,Void,Void> {

        private String status = "";
        @Override
        protected Void doInBackground(Void... voids) {
            status = HttpHandler.postRequest(MapsActivity.Location_API_URL, location);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(AddLocationActivity.this,
                    status, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddLocationActivity.this,
                    MapsActivity.class);
            setResult(101, intent);
            finish();
        }
    }
}