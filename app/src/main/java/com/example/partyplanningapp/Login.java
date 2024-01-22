package com.example.partyplanningapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        usernameEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.button);

        // Set click listener for Login button
        loginButton.setOnClickListener(view -> login());

        TextView btn = findViewById(R.id.textSign);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
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

                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            openPost();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
        }
    }

    public void openPost() {
        startActivity(new Intent(this,Post.class));
    }

}