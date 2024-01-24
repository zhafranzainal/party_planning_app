package com.example.partyplanningapp;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostActivity extends AppCompatActivity {

    private ImageView imageView;
    private FloatingActionButton button;
    private TextView userEmailTextView;
    private Button backButton;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imageView = findViewById(R.id.imageView9);
        button = findViewById(R.id.floatingActionButton);
        userEmailTextView = findViewById(R.id.textView10);
        backButton = findViewById(R.id.button3);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Display user's email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            userEmailTextView.setText(userEmail);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(PostActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, HomePage.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                // Upload image to Firebase Storage
                uploadImage(uri);
                imageView.setImageURI(uri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        // Get the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to store the image with the user's ID as the filename
            StorageReference userImageRef = storageRef.child("images/" + userId + ".jpg");

            // Upload image to Firebase Storage
            userImageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, get the download URL
                        userImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the download URL to the Firebase Database
                            saveImageUrlToDatabase(userId, uri.toString());
                            imageView.setImageURI(imageUri);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        // TODO: Handle the error
                    });
        }
    }

    private void saveImageUrlToDatabase(String userId, String imageUrl) {
        // Update the user's data in the Firebase Realtime Database
        usersRef.child(userId).child("image_url").setValue(imageUrl);
    }
}