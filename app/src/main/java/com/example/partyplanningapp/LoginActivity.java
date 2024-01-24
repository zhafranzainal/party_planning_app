package com.example.partyplanningapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        usernameEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.button);

        // Set click listener for LoginActivity button
        loginButton.setOnClickListener(view -> login());

        TextView btn = findViewById(R.id.textSign);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void login() {
        String email = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Validate username and password
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Check if it's the first login
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser != null) {
                                checkFirstLogin(currentUser.getUid());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "LoginActivity failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkFirstLogin(String userId) {
        DatabaseReference userRef = usersRef.child(userId).child("firstLogin");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isFirstLogin = snapshot.getValue(Boolean.class);

                if (isFirstLogin == null || isFirstLogin) {
                    // If it's the first login, set the flag to false and open PostActivity activity
                    userRef.setValue(false);
                    openPost();
                } else {
                    // If it's not the first login, open Home activity
                    openHomePage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                // You might want to log the error or take appropriate action
            }
        });
    }

    private void openPost() {
        startActivity(new Intent(this, PostActivity.class));
        finish(); // Finish the current activity to prevent going back to it
    }

    private void openHomePage() {
        startActivity(new Intent(this, HomePage.class));
        finish(); // Finish the current activity to prevent going back to it
    }
}