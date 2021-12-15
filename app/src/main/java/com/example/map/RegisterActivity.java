package com.example.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText fullName,email,password,phone;
    Button registerBtn,LoginButton;
    boolean valid = true;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        phone = findViewById(R.id.registerPhone);
        registerBtn = findViewById(R.id.registerBtn);
        LoginButton = findViewById(R.id.LoginButton);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        BottomNavigationView navigationView =findViewById(R.id.navbar);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_user:;
                        Toast.makeText(RegisterActivity.this, "User", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_location:;
                        Toast.makeText(RegisterActivity.this, "Location", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(RegisterActivity.this, MapsActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_list:;
                        Toast.makeText(RegisterActivity.this, "List", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(RegisterActivity.this, ListActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkField(fullName);
                checkField(email);
                checkField(password);
                checkField(phone);

                if (valid) {

                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    DocumentReference df = fStore.collection(DatabasePath.USER).document(user.getUid());
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("username", fullName.getText().toString());
                                    userInfo.put("email", email.getText().toString());
                                    userInfo.put("password", password.getText().toString());
                                    userInfo.put("phone number", phone.getText().toString());
                                    userInfo.put("isUser",1);

                                    df.set(userInfo);


                                    FirebaseAuth.getInstance().signOut();
                                }
                            }).addOnFailureListener((@NonNull Exception e) -> {
                            Toast.makeText(RegisterActivity.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                        });
                }
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

    }


    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
    public void openLoginActivity(){
        Intent intent = new Intent(this, com.example.map.LoginActivity.class);
        startActivity(intent);
    }
}