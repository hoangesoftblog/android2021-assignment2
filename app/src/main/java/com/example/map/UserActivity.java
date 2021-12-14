package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {
    Button logoutbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        logoutbutton = findViewById(R.id.logout_button);

        BottomNavigationView navigationView =findViewById(R.id.navbar);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_user:;
                        Toast.makeText(UserActivity.this, "User", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_location:;
                        Toast.makeText(UserActivity.this, "Location", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(UserActivity.this, MapsActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_list:;
                        Toast.makeText(UserActivity.this, "List", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(UserActivity.this, ListActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}