package com.example.shoping;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView profileImage;
    private TextView profileName;
    private FloatingActionButton editImageButton;
    private ImageView editNameButton;
    private ImageView backArrow;
    private ImageView settingsButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Initialize views
        initializeViews();

        // Set click listeners
        setClickListeners();

        // Load user data
        loadUserData();
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        editImageButton = findViewById(R.id.editImageButton);
        editNameButton = findViewById(R.id.editNameButton);
        backArrow = findViewById(R.id.backArrow);
        settingsButton = findViewById(R.id.settingsButton);
    }

    private void setClickListeners() {
        editImageButton.setOnClickListener(v -> openImageChooser());
        editNameButton.setOnClickListener(v -> showEditNameDialog());
        backArrow.setOnClickListener(v -> onBackPressed());
        settingsButton.setOnClickListener(v -> Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show());
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Load profile image
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.profile_image)
                        .into(profileImage);
            }

            // Load name
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                profileName.setText(displayName);
            }

            // Load additional user data from Firestore if needed
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fullName");
                            if (name != null && !name.isEmpty()) {
                                profileName.setText(name);
                            }
                        }
                    });
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profileImage);
            uploadProfilePicture();
        }
    }

    private void uploadProfilePicture() {
        if (imageUri == null) return;

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        StorageReference fileReference = storageRef.child("profile_images/" + user.getUid() + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }))
                .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show());
    }

    private void showEditNameDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
        TextInputEditText nameEditText = dialogView.findViewById(R.id.nameEditText);

        // Pre-fill with current name
        nameEditText.setText(profileName.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameEditText.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        updateUserName(newName);
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateUserName(String newName) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(EditProfileActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update Firebase Auth Profile
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Update UI
                    profileName.setText(newName);

                    // Create a new user document or update existing one
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("fullName", newName);
                    userData.put("email", user.getEmail());

                    db.collection("users").document(user.getUid())
                            .set(userData, SetOptions.merge())
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(EditProfileActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(EditProfileActivity.this, "Failed to update name in database: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("EditProfileActivity", "Firestore update failed", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to update name: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("EditProfileActivity", "Auth profile update failed", e);
                });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}