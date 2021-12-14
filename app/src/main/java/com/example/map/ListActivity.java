package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
          startActivity(new Intent(ListActivity.this, LoginActivity.class));

        }
    }
}