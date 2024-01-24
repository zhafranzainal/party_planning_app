package com.example.partyplanningapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText emailEditText;
    private Button signUpButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.editTextText2);
        passwordEditText = findViewById(R.id.editTextTextPassword2);
        confirmPasswordEditText = findViewById(R.id.editTextTextPassword3);
        signUpButton = findViewById(R.id.button2);

        // Set click listener for SignUp button
        signUpButton.setOnClickListener(view -> signUp());

        TextView btn = findViewById(R.id.textLogin2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void signUp() {
        // Get entered password, confirmed password, and email address
        String password = passwordEditText.getText().toString();
        String confirmedPassword = confirmPasswordEditText.getText().toString();
        String email = emailEditText.getText().toString();

        // Validate password and confirmation
        if (isValidPassword(password) && isValidConfirmation(password, confirmedPassword)) {
            // Use Firebase Authentication to create a new user
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Registration success
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            // Now, you can navigate to another activity or perform additional actions
                            new Handler().postDelayed(() -> {
                                // Start a new activity after a delay of 2 seconds
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);

                                // Finish the current activity (optional)
                                finish();
                            }, 500);
                        } else {
                            // If registration fails, display a message to the user.
                            Toast.makeText(this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Password or confirmation is not valid, display an error message
            if (!isValidPassword(password)) {
                passwordEditText.setError("Password must be at least 6 characters long");
            }
            if (!isValidConfirmation(password, confirmedPassword)) {
                confirmPasswordEditText.setError("Passwords do not match");
            }
        }
    }

    private boolean isValidPassword(String password) {
        // Add your password validation logic here
        // For example, require a minimum length of 6 characters
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    private boolean isValidConfirmation(String password, String confirmedPassword) {
        // Check if the confirmed password matches the entered password
        return !TextUtils.isEmpty(confirmedPassword) && confirmedPassword.equals(password);
    }
}